package com.kevindev.animeapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_track")
data class NotificationTrackEntity(
    @PrimaryKey val animeAnilistId: Int,
    val lastCheckedEpisode: Int,
    val lastCheckedAt: Long,
)
