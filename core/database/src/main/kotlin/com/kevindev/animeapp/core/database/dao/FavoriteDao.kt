package com.kevindev.animeapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevindev.animeapp.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT animeAnilistId FROM favorites WHERE profileId = :profileId ORDER BY addedAt DESC")
    fun getFavoriteIds(profileId: Long): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE profileId = :profileId AND animeAnilistId = :animeId)")
    fun isFavorite(profileId: Long, animeId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE profileId = :profileId AND animeAnilistId = :animeId")
    suspend fun removeFavorite(profileId: Long, animeId: Int)

    @Query("SELECT animeAnilistId FROM favorites GROUP BY animeAnilistId")
    suspend fun getAllFavoritedAnimeIds(): List<Int>
}
