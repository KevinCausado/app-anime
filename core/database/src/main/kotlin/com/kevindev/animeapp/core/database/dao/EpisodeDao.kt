package com.kevindev.animeapp.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kevindev.animeapp.core.database.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE animeAnilistId = :animeId ORDER BY episodeNumber ASC")
    fun getEpisodesByAnime(animeId: Int): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE animeAnilistId = :animeId AND episodeNumber = :number")
    suspend fun getEpisode(animeId: Int, number: Int): EpisodeEntity?

    @Upsert
    suspend fun upsertEpisodes(episodes: List<EpisodeEntity>)

    @Query("DELETE FROM episodes WHERE animeAnilistId = :animeId")
    suspend fun deleteEpisodesForAnime(animeId: Int)
}
