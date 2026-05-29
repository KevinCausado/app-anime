package com.kevindev.animeapp.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kevindev.animeapp.core.database.entity.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime WHERE anilistId = :id")
    fun getAnimeById(id: Int): Flow<AnimeEntity?>

    @Query("SELECT * FROM anime WHERE anilistId IN (:ids)")
    fun getAnimeByIds(ids: List<Int>): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime WHERE cachedAt < :expiryMs")
    suspend fun getStaleAnime(expiryMs: Long): List<AnimeEntity>

    @Upsert
    suspend fun upsertAnimes(animes: List<AnimeEntity>)

    @Upsert
    suspend fun upsertAnime(anime: AnimeEntity)

    @Query("DELETE FROM anime WHERE anilistId = :id")
    suspend fun deleteAnime(id: Int)

    @Query("DELETE FROM anime WHERE cachedAt < :expiryMs")
    suspend fun deleteStaleAnime(expiryMs: Long)
}
