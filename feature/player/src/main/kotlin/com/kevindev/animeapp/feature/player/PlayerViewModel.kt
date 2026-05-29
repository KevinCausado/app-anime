package com.kevindev.animeapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevindev.animeapp.core.database.dao.WatchHistoryDao
import com.kevindev.animeapp.core.database.entity.WatchHistoryEntity
import com.kevindev.animeapp.core.datastore.ProfileDataStore
import com.kevindev.animeapp.core.network.consumet.AnimekaiApi
import com.kevindev.animeapp.core.network.consumet.ConsometWatchResponseDto
import com.kevindev.animeapp.core.network.consumet.HiAnimeApi
import com.kevindev.animeapp.core.network.consumet.toStreamingInfo
import com.kevindev.animeapp.feature.player.mvi.PlayerEffect
import com.kevindev.animeapp.feature.player.mvi.PlayerIntent
import com.kevindev.animeapp.feature.player.mvi.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val hiAnimeApi: HiAnimeApi,
    private val animekaiApi: AnimekaiApi,
    private val watchHistoryDao: WatchHistoryDao,
    private val profileDataStore: ProfileDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerState>(PlayerState.Idle)
    val state: StateFlow<PlayerState> = _state.asStateFlow()

    private val _effects = Channel<PlayerEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var animeId: Int = 0
    private var episodeId: String = ""
    private var episodeNumber: Int = 0
    private var progressJob: Job? = null

    fun onIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.Load -> load(intent.episodeId, intent.animeId)
            is PlayerIntent.Play -> updateReady { copy(isPlaying = true) }
            is PlayerIntent.Pause -> {
                updateReady { copy(isPlaying = false) }
                saveProgress()
            }
            is PlayerIntent.SeekTo -> {
                updateReady { copy(positionMs = intent.positionMs) }
                viewModelScope.launch { _effects.send(PlayerEffect.SeekPlayer(intent.positionMs)) }
            }
            is PlayerIntent.SkipIntro -> {
                val end = (state.value as? PlayerState.Ready)?.introEnd ?: return
                updateReady { copy(positionMs = end) }
                viewModelScope.launch { _effects.send(PlayerEffect.SeekPlayer(end)) }
            }
            is PlayerIntent.SkipOutro -> {
                val start = (state.value as? PlayerState.Ready)?.outroStart ?: return
                updateReady { copy(positionMs = start + 1000) }
                viewModelScope.launch { _effects.send(PlayerEffect.SeekPlayer(start + 1000)) }
            }
            is PlayerIntent.ChangeSpeed -> updateReady { copy(playbackSpeed = intent.speed) }
            is PlayerIntent.SelectSubtitle -> updateReady { copy(selectedSubtitleIndex = intent.trackIndex) }
            is PlayerIntent.ToggleControls -> updateReady { copy(showControls = !showControls) }
            is PlayerIntent.Back -> {
                saveProgress()
                viewModelScope.launch { _effects.send(PlayerEffect.NavigateBack) }
            }
        }
    }

    private fun load(episodeId: String, animeId: Int) {
        this.episodeId = episodeId
        this.animeId = animeId
        viewModelScope.launch {
            _state.value = PlayerState.Loading
            try {
                val watchResponse = fetchStream(episodeId)
                val streamingInfo = watchResponse.toStreamingInfo()

                val savedProgress = profileDataStore.activeProfileId.firstOrNull()?.let { profileId ->
                    watchHistoryDao.getProgress(profileId, animeId, episodeNumber).firstOrNull()?.progressMs ?: 0L
                } ?: 0L

                _state.value = PlayerState.Ready(
                    streamingInfo = streamingInfo,
                    isPlaying = true,
                    positionMs = savedProgress,
                    durationMs = 0L,
                    bufferedMs = 0L,
                    playbackSpeed = 1f,
                    selectedSubtitleIndex = 0,
                    showControls = true,
                    introEnd = watchResponse.intro?.end?.toLong()?.times(1000),
                    outroStart = watchResponse.outro?.start?.toLong()?.times(1000),
                )

                startProgressTimer()
            } catch (e: Exception) {
                _state.value = PlayerState.Error(e.message ?: "Error al cargar el video")
            }
        }
    }

    private suspend fun fetchStream(episodeId: String): ConsometWatchResponseDto {
        return try {
            hiAnimeApi.watch(episodeId = episodeId)
        } catch (e: Exception) {
            animekaiApi.watch(episodeId = episodeId)
        }
    }

    fun updatePosition(positionMs: Long, durationMs: Long, bufferedMs: Long) {
        updateReady {
            copy(positionMs = positionMs, durationMs = durationMs, bufferedMs = bufferedMs)
        }
    }

    private fun startProgressTimer() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                delay(10_000)
                saveProgress()
            }
        }
    }

    private fun saveProgress() {
        val state = _state.value as? PlayerState.Ready ?: return
        if (state.positionMs == 0L) return
        viewModelScope.launch {
            val profileId = profileDataStore.activeProfileId.firstOrNull() ?: return@launch
            watchHistoryDao.upsertProgress(
                WatchHistoryEntity(
                    profileId = profileId,
                    animeAnilistId = animeId,
                    episodeNumber = episodeNumber,
                    progressMs = state.positionMs,
                    durationMs = state.durationMs,
                    completedAt = if (state.progressFraction >= 0.9f) System.currentTimeMillis() else null,
                    updatedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    private fun updateReady(block: PlayerState.Ready.() -> PlayerState.Ready) {
        val current = _state.value as? PlayerState.Ready ?: return
        _state.value = current.block()
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
        saveProgress()
    }
}
