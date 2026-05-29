package com.kevindev.animeapp.core.model

data class Episode(
    val id: String,
    val animeId: Int,
    val number: Int,
    val title: String?,
    val description: String?,
    val thumbnail: String?,
    val isFiller: Boolean,
    val airDate: String?,
)
