package com.kevindev.animeapp.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntity::class,
            parentColumns = ["anilistId"],
            childColumns = ["animeAnilistId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("animeAnilistId")],
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val animeAnilistId: Int,
    val episodeNumber: Int,
    val title: String?,
    val description: String?,
    val thumbnail: String?,
    val consumetEpisodeId: String,
    val isFiller: Boolean,
    val airDate: String?,
)
