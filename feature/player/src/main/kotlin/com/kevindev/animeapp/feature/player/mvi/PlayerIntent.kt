package com.kevindev.animeapp.feature.player.mvi

sealed interface PlayerIntent {
    data class Load(val episodeId: String, val animeId: Int) : PlayerIntent
    data object Play : PlayerIntent
    data object Pause : PlayerIntent
    data class SeekTo(val positionMs: Long) : PlayerIntent
    data object SkipIntro : PlayerIntent
    data object SkipOutro : PlayerIntent
    data class ChangeSpeed(val speed: Float) : PlayerIntent
    data class SelectSubtitle(val trackIndex: Int) : PlayerIntent
    data object ToggleControls : PlayerIntent
    data object Back : PlayerIntent
}
