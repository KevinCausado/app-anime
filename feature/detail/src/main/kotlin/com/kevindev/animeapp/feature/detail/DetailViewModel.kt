package com.kevindev.animeapp.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.kevindev.animeapp.core.database.dao.FavoriteDao
import com.kevindev.animeapp.core.database.dao.WatchHistoryDao
import com.kevindev.animeapp.core.database.dao.WatchlistDao
import com.kevindev.animeapp.core.database.entity.FavoriteEntity
import com.kevindev.animeapp.core.database.entity.WatchlistEntity
import com.kevindev.animeapp.core.datastore.ProfileDataStore
import com.kevindev.animeapp.core.model.Anime
import com.kevindev.animeapp.core.model.Episode
import com.kevindev.animeapp.core.network.apollo.toAnime
import com.kevindev.animeapp.core.network.consumet.ConsometEpisodeDto
import com.kevindev.animeapp.core.network.consumet.HiAnimeApi
import com.kevindev.animeapp.core.network.graphql.AnimeDetailQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RelatedAnime(
    val id: Int,
    val titleRomaji: String,
    val coverImageUrl: String,
    val relationType: String,
)

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(
        val anime: Anime,
        val episodes: List<Episode>,
        val related: List<RelatedAnime>,
        val recommendations: List<Anime>,
        val isFavorite: Boolean,
        val isInWatchlist: Boolean,
        val lastWatchedEpisode: Int?,
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apolloClient: ApolloClient,
    private val hiAnimeApi: HiAnimeApi,
    private val favoriteDao: FavoriteDao,
    private val watchlistDao: WatchlistDao,
    private val watchHistoryDao: WatchHistoryDao,
    private val profileDataStore: ProfileDataStore,
) : ViewModel() {

    private val animeId: Int = checkNotNull(savedStateHandle["animeId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val profileId = profileDataStore.activeProfileId.firstOrNull()

                val detailDeferred = async { fetchDetail() }
                val episodesDeferred = async { fetchEpisodes() }

                val (anime, related, recommendations) = detailDeferred.await()
                val episodes = episodesDeferred.await()

                val isFavorite = if (profileId != null) {
                    favoriteDao.isFavorite(profileId, animeId).firstOrNull() ?: false
                } else false

                val isInWatchlist = if (profileId != null) {
                    watchlistDao.isInWatchlist(profileId, animeId).firstOrNull() ?: false
                } else false

                val lastWatched = if (profileId != null) {
                    watchHistoryDao.getLastWatchedEpisode(profileId, animeId).firstOrNull()?.episodeNumber
                } else null

                _uiState.value = DetailUiState.Success(
                    anime = anime,
                    episodes = episodes,
                    related = related,
                    recommendations = recommendations,
                    isFavorite = isFavorite,
                    isInWatchlist = isInWatchlist,
                    lastWatchedEpisode = lastWatched,
                )
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Error al cargar")
            }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value as? DetailUiState.Success ?: return
        viewModelScope.launch {
            val profileId = profileDataStore.activeProfileId.firstOrNull() ?: return@launch
            if (state.isFavorite) {
                favoriteDao.removeFavorite(profileId, animeId)
            } else {
                favoriteDao.addFavorite(
                    FavoriteEntity(profileId = profileId, animeAnilistId = animeId, addedAt = System.currentTimeMillis())
                )
            }
            _uiState.value = state.copy(isFavorite = !state.isFavorite)
        }
    }

    fun toggleWatchlist() {
        val state = _uiState.value as? DetailUiState.Success ?: return
        viewModelScope.launch {
            val profileId = profileDataStore.activeProfileId.firstOrNull() ?: return@launch
            if (state.isInWatchlist) {
                watchlistDao.removeFromWatchlist(profileId, animeId)
            } else {
                watchlistDao.addToWatchlist(
                    WatchlistEntity(profileId = profileId, animeAnilistId = animeId, addedAt = System.currentTimeMillis())
                )
            }
            _uiState.value = state.copy(isInWatchlist = !state.isInWatchlist)
        }
    }

    private suspend fun fetchDetail(): Triple<Anime, List<RelatedAnime>, List<Anime>> {
        val response = apolloClient.query(
            AnimeDetailQuery(id = Optional.present(animeId))
        ).execute()

        val media = response.data?.Media
            ?: throw Exception("Anime no encontrado")

        val anime = media.animeFragment.toAnime()

        val related = media.relations?.edges?.mapNotNull { edge ->
            val node = edge?.node ?: return@mapNotNull null
            if (node.type?.name != "ANIME") return@mapNotNull null
            RelatedAnime(
                id = node.id,
                titleRomaji = node.title?.romaji ?: "",
                coverImageUrl = node.coverImage?.large ?: "",
                relationType = edge.relationType?.name ?: "",
            )
        } ?: emptyList()

        val recommendations = media.recommendations?.nodes?.mapNotNull {
            it?.mediaRecommendation?.animeFragment?.toAnime()
        } ?: emptyList()

        return Triple(anime, related, recommendations)
    }

    private suspend fun fetchEpisodes(): List<Episode> {
        return try {
            val searchResult = hiAnimeApi.search(query = animeId.toString())
            val consumetId = searchResult.results.firstOrNull()?.id ?: return emptyList()
            val info = hiAnimeApi.getEpisodes(consumetId)
            info.episodes.map { it.toEpisode(animeId) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun ConsometEpisodeDto.toEpisode(animeAnilistId: Int) = Episode(
        id = id,
        animeId = animeAnilistId,
        number = number,
        title = title,
        description = null,
        thumbnail = null,
        isFiller = isFiller,
        airDate = null,
    )
}
