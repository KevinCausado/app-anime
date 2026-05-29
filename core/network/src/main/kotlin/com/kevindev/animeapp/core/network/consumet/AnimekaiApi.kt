package com.kevindev.animeapp.core.network.consumet

import retrofit2.http.GET
import retrofit2.http.Query

interface AnimekaiApi {

    @GET("anime/animekai/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
    ): ConsometSearchResultDto

    @GET("anime/animekai/info")
    suspend fun getInfo(
        @Query("id") id: String,
    ): ConsometAnimeInfoDto

    @GET("anime/animekai/watch")
    suspend fun watch(
        @Query("episodeId") episodeId: String,
        @Query("category") category: String = "sub",
    ): ConsometWatchResponseDto
}
