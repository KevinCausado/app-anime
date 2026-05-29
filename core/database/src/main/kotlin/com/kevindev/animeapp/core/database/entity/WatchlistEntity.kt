package com.kevindev.animeapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "watchlist",
    primaryKeys = ["profileId", "animeAnilistId"],
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("profileId")],
)
data class WatchlistEntity(
    val profileId: Long,
    val animeAnilistId: Int,
    val addedAt: Long,
)
