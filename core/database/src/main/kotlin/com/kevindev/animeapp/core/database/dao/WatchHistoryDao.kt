package com.kevindev.animeapp.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kevindev.animeapp.core.database.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("""
        SELECT * FROM watch_history
        WHERE profileId = :profileId AND animeAnilistId = :animeId AND episodeNumber = :episodeNumber
        LIMIT 1
    """)
    fun getProgress(profileId: Long, animeId: Int, episodeNumber: Int): Flow<WatchHistoryEntity?>

    @Query("""
        SELECT * FROM watch_history
        WHERE profileId = :profileId
        ORDER BY updatedAt DESC
        LIMIT :limit
    """)
    fun getRecentHistory(profileId: Long, limit: Int = 20): Flow<List<WatchHistoryEntity>>

    @Query("""
        SELECT * FROM watch_history
        WHERE profileId = :profileId AND animeAnilistId = :animeId
        ORDER BY updatedAt DESC
        LIMIT 1
    """)
    fun getLastWatchedEpisode(profileId: Long, animeId: Int): Flow<WatchHistoryEntity?>

    @Upsert
    suspend fun upsertProgress(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE profileId = :profileId AND animeAnilistId = :animeId AND episodeNumber = :episodeNumber")
    suspend fun deleteProgress(profileId: Long, animeId: Int, episodeNumber: Int)

    @Query("DELETE FROM watch_history WHERE profileId = :profileId")
    suspend fun clearHistoryForProfile(profileId: Long)
}
