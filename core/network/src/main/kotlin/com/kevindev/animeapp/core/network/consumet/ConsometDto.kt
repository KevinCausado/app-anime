package com.kevindev.animeapp.core.network.consumet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConsometSearchResultDto(
    @SerialName("currentPage") val currentPage: Int = 1,
    @SerialName("hasNextPage") val hasNextPage: Boolean = false,
    @SerialName("results") val results: List<ConsometAnimeResultDto> = emptyList(),
)

@Serializable
data class ConsometAnimeResultDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("releaseDate") val releaseDate: String? = null,
    @SerialName("subOrDub") val subOrDub: String? = null,
)

@Serializable
data class ConsometAnimeInfoDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("url") val url: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("releaseDate") val releaseDate: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("totalEpisodes") val totalEpisodes: Int? = null,
    @SerialName("episodes") val episodes: List<ConsometEpisodeDto> = emptyList(),
)

@Serializable
data class ConsometEpisodeDto(
    @SerialName("id") val id: String,
    @SerialName("number") val number: Int,
    @SerialName("title") val title: String? = null,
    @SerialName("isFiller") val isFiller: Boolean = false,
    @SerialName("url") val url: String? = null,
)

@Serializable
data class ConsometWatchResponseDto(
    @SerialName("sources") val sources: List<ConsometSourceDto> = emptyList(),
    @SerialName("subtitles") val subtitles: List<ConsometSubtitleDto> = emptyList(),
    @SerialName("intro") val intro: ConsometIntroDto? = null,
    @SerialName("outro") val outro: ConsometIntroDto? = null,
)

@Serializable
data class ConsometSourceDto(
    @SerialName("url") val url: String,
    @SerialName("quality") val quality: String = "auto",
    @SerialName("isM3U8") val isM3U8: Boolean = true,
)

@Serializable
data class ConsometSubtitleDto(
    @SerialName("url") val url: String,
    @SerialName("lang") val lang: String,
    @SerialName("label") val label: String? = null,
    @SerialName("kind") val kind: String? = null,
)

@Serializable
data class ConsometIntroDto(
    @SerialName("start") val start: Int = 0,
    @SerialName("end") val end: Int = 0,
)
