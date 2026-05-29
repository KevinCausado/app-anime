package com.kevindev.animeapp.core.network.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.HttpNetworkTransport
import com.kevindev.animeapp.core.network.BuildConfig
import com.kevindev.animeapp.core.network.consumet.AnimekaiApi
import com.kevindev.animeapp.core.network.consumet.HiAnimeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.CONSUMET_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideHiAnimeApi(retrofit: Retrofit): HiAnimeApi =
        retrofit.create(HiAnimeApi::class.java)

    @Provides
    @Singleton
    fun provideAnimekaiApi(retrofit: Retrofit): AnimekaiApi =
        retrofit.create(AnimekaiApi::class.java)

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .networkTransport(
            HttpNetworkTransport.Builder()
                .serverUrl("https://graphql.anilist.co")
                .build()
        )
        .build()
}
