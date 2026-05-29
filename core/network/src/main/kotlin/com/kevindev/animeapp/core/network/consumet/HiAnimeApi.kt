package com.kevindev.animeapp.core.network.consumet

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HiAnimeApi {

    @GET("anime/hianime/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
    ): ConsometSearchResultDto

    @GET("anime/hianime/info")
    suspend fun getInfo(
        @Query("id") id: String,
    ): ConsometAnimeInfoDto

    @GET("anime/hianime/watch")
    suspend fun watch(
        @Query("episodeId") episodeId: String,
        @Query("server") server: String = "hd-1",
        @Query("category") category: String = "sub",
    ): ConsometWatchResponseDto

    @GET("anime/hianime/episodes/{id}")
    suspend fun getEpisodes(
        @Path("id") animeId: String,
    ): HiAnimeEpisodesResponseDto
}

@kotlinx.serialization.Serializable
data class HiAnimeEpisodesResponseDto(
    @kotlinx.serialization.SerialName("totalEpisodes") val totalEpisodes: Int = 0,
    @kotlinx.serialization.SerialName("episodes") val episodes: List<ConsometEpisodeDto> = emptyList(),
)
