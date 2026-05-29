package com.kevindev.animeapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey val anilistId: Int,
    val titleRomaji: String,
    val titleEnglish: String?,
    val titleNative: String?,
    val coverImageUrl: String,
    val bannerImageUrl: String?,
    val description: String?,
    val status: String,
    val format: String,
    val episodes: Int?,
    val duration: Int?,
    val averageScore: Int?,
    val genres: String,
    val season: String?,
    val seasonYear: Int?,
    val studios: String,
    val nextAiringEpisode: Int?,
    val nextAiringAt: Long?,
    val isAdult: Boolean,
    val cachedAt: Long,
)
