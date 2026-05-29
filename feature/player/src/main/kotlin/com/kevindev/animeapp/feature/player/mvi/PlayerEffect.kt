package com.kevindev.animeapp.feature.player.mvi

sealed interface PlayerEffect {
    data object NavigateBack : PlayerEffect
    data class ShowError(val message: String) : PlayerEffect
    data class SeekPlayer(val positionMs: Long) : PlayerEffect
}
