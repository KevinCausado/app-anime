package com.kevindev.animeapp.core.model

data class Anime(
    val id: Int,
    val titleRomaji: String,
    val titleEnglish: String?,
    val titleNative: String?,
    val coverImageUrl: String,
    val bannerImageUrl: String?,
    val description: String?,
    val status: AnimeStatus,
    val format: AnimeFormat,
    val episodes: Int?,
    val duration: Int?,
    val averageScore: Int?,
    val genres: List<String>,
    val season: AnimeSeason?,
    val seasonYear: Int?,
    val studios: List<String>,
    val nextAiringEpisode: Int?,
    val nextAiringAt: Long?,
    val isAdult: Boolean,
)

enum class AnimeStatus {
    FINISHED, RELEASING, NOT_YET_RELEASED, CANCELLED, HIATUS, UNKNOWN
}

enum class AnimeFormat {
    TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC, MANGA, NOVEL, ONE_SHOT, UNKNOWN
}

enum class AnimeSeason {
    WINTER, SPRING, SUMMER, FALL
}
