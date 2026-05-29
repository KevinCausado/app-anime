package com.kevindev.animeapp.core.model

data class WatchProgress(
    val profileId: Long,
    val animeId: Int,
    val episodeNumber: Int,
    val progressMs: Long,
    val durationMs: Long,
    val updatedAt: Long,
) {
    val progressFraction: Float
        get() = if (durationMs > 0) progressMs.toFloat() / durationMs else 0f

    val isCompleted: Boolean
        get() = progressFraction >= 0.9f
}
