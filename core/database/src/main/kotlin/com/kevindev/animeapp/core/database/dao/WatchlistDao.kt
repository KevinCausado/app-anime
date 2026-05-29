package com.kevindev.animeapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevindev.animeapp.core.database.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT animeAnilistId FROM watchlist WHERE profileId = :profileId ORDER BY addedAt DESC")
    fun getWatchlistIds(profileId: Long): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE profileId = :profileId AND animeAnilistId = :animeId)")
    fun isInWatchlist(profileId: Long, animeId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToWatchlist(watchlist: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE profileId = :profileId AND animeAnilistId = :animeId")
    suspend fun removeFromWatchlist(profileId: Long, animeId: Int)
}
