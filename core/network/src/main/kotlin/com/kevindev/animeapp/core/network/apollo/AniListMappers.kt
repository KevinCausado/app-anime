package com.kevindev.animeapp.core.network.apollo

import com.kevindev.animeapp.core.model.Anime
import com.kevindev.animeapp.core.model.AnimeFormat
import com.kevindev.animeapp.core.model.AnimeSeason
import com.kevindev.animeapp.core.model.AnimeStatus
import com.kevindev.animeapp.core.network.graphql.fragment.AnimeFragment
import com.kevindev.animeapp.core.network.graphql.type.MediaFormat
import com.kevindev.animeapp.core.network.graphql.type.MediaSeason
import com.kevindev.animeapp.core.network.graphql.type.MediaStatus

fun AnimeFragment.toAnime() = Anime(
    id = id,
    titleRomaji = title?.romaji ?: "",
    titleEnglish = title?.english,
    titleNative = title?.native,
    coverImageUrl = coverImage?.extraLarge ?: coverImage?.large ?: "",
    bannerImageUrl = bannerImage,
    description = description,
    status = status?.toAnimeStatus() ?: AnimeStatus.UNKNOWN,
    format = format?.toAnimeFormat() ?: AnimeFormat.UNKNOWN,
    episodes = episodes,
    duration = duration,
    averageScore = averageScore,
    genres = genres?.filterNotNull() ?: emptyList(),
    season = season?.toAnimeSeason(),
    seasonYear = seasonYear,
    studios = studios?.nodes?.mapNotNull { it?.name } ?: emptyList(),
    nextAiringEpisode = nextAiringEpisode?.episode,
    nextAiringAt = nextAiringEpisode?.airingAt?.toLong(),
    isAdult = isAdult ?: false,
)

private fun MediaStatus.toAnimeStatus() = when (this) {
    MediaStatus.FINISHED -> AnimeStatus.FINISHED
    MediaStatus.RELEASING -> AnimeStatus.RELEASING
    MediaStatus.NOT_YET_RELEASED -> AnimeStatus.NOT_YET_RELEASED
    MediaStatus.CANCELLED -> AnimeStatus.CANCELLED
    MediaStatus.HIATUS -> AnimeStatus.HIATUS
    else -> AnimeStatus.UNKNOWN
}

private fun MediaFormat.toAnimeFormat() = when (this) {
    MediaFormat.TV -> AnimeFormat.TV
    MediaFormat.TV_SHORT -> AnimeFormat.TV_SHORT
    MediaFormat.MOVIE -> AnimeFormat.MOVIE
    MediaFormat.SPECIAL -> AnimeFormat.SPECIAL
    MediaFormat.OVA -> AnimeFormat.OVA
    MediaFormat.ONA -> AnimeFormat.ONA
    MediaFormat.MUSIC -> AnimeFormat.MUSIC
    else -> AnimeFormat.UNKNOWN
}

private fun MediaSeason.toAnimeSeason() = when (this) {
    MediaSeason.WINTER -> AnimeSeason.WINTER
    MediaSeason.SPRING -> AnimeSeason.SPRING
    MediaSeason.SUMMER -> AnimeSeason.SUMMER
    MediaSeason.FALL -> AnimeSeason.FALL
    else -> null
}
