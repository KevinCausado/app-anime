package com.kevindev.animeapp.feature.player.mvi

import com.kevindev.animeapp.core.model.EpisodeStreamingInfo
import com.kevindev.animeapp.core.model.SubtitleTrack

sealed interface PlayerState {
    data object Idle : PlayerState
    data object Loading : PlayerState
    data class Ready(
        val streamingInfo: EpisodeStreamingInfo,
        val isPlaying: Boolean,
        val positionMs: Long,
        val durationMs: Long,
        val bufferedMs: Long,
        val playbackSpeed: Float,
        val selectedSubtitleIndex: Int,
        val showControls: Boolean,
        val introEnd: Long?,
        val outroStart: Long?,
    ) : PlayerState {
        val progressFraction: Float
            get() = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f

        val showSkipIntro: Boolean
            get() = introEnd != null && positionMs < introEnd

        val showSkipOutro: Boolean
            get() = outroStart != null && positionMs >= outroStart

        val currentSubtitle: SubtitleTrack?
            get() = streamingInfo.subtitles.getOrNull(selectedSubtitleIndex)
    }
    data class Error(val message: String) : PlayerState
}
