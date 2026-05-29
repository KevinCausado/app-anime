package com.kevindev.animeapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kevindev.animeapp.core.database.dao.AnimeDao
import com.kevindev.animeapp.core.database.dao.EpisodeDao
import com.kevindev.animeapp.core.database.dao.FavoriteDao
import com.kevindev.animeapp.core.database.dao.NotificationTrackDao
import com.kevindev.animeapp.core.database.dao.ProfileDao
import com.kevindev.animeapp.core.database.dao.WatchHistoryDao
import com.kevindev.animeapp.core.database.dao.WatchlistDao
import com.kevindev.animeapp.core.database.entity.AnimeEntity
import com.kevindev.animeapp.core.database.entity.EpisodeEntity
import com.kevindev.animeapp.core.database.entity.FavoriteEntity
import com.kevindev.animeapp.core.database.entity.NotificationTrackEntity
import com.kevindev.animeapp.core.database.entity.ProfileEntity
import com.kevindev.animeapp.core.database.entity.WatchHistoryEntity
import com.kevindev.animeapp.core.database.entity.WatchlistEntity

@Database(
    entities = [
        ProfileEntity::class,
        AnimeEntity::class,
        EpisodeEntity::class,
        WatchHistoryEntity::class,
        FavoriteEntity::class,
        WatchlistEntity::class,
        NotificationTrackEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(AnimeTypeConverters::class)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun animeDao(): AnimeDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun notificationTrackDao(): NotificationTrackDao
}
