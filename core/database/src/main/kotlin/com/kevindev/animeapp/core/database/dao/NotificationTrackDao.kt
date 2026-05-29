package com.kevindev.animeapp.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kevindev.animeapp.core.database.entity.NotificationTrackEntity

@Dao
interface NotificationTrackDao {
    @Query("SELECT * FROM notification_track")
    suspend fun getAll(): List<NotificationTrackEntity>

    @Query("SELECT * FROM notification_track WHERE animeAnilistId = :animeId")
    suspend fun getByAnimeId(animeId: Int): NotificationTrackEntity?

    @Upsert
    suspend fun upsert(track: NotificationTrackEntity)

    @Query("DELETE FROM notification_track WHERE animeAnilistId = :animeId")
    suspend fun delete(animeId: Int)
}
