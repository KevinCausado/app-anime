package com.kevindev.animeapp.core.database.di

import android.content.Context
import androidx.room.Room
import com.kevindev.animeapp.core.database.AnimeDatabase
import com.kevindev.animeapp.core.database.dao.AnimeDao
import com.kevindev.animeapp.core.database.dao.EpisodeDao
import com.kevindev.animeapp.core.database.dao.FavoriteDao
import com.kevindev.animeapp.core.database.dao.NotificationTrackDao
import com.kevindev.animeapp.core.database.dao.ProfileDao
import com.kevindev.animeapp.core.database.dao.WatchHistoryDao
import com.kevindev.animeapp.core.database.dao.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAnimeDatabase(@ApplicationContext context: Context): AnimeDatabase =
        Room.databaseBuilder(context, AnimeDatabase::class.java, "anime_db")
            .build()

    @Provides fun provideProfileDao(db: AnimeDatabase): ProfileDao = db.profileDao()
    @Provides fun provideAnimeDao(db: AnimeDatabase): AnimeDao = db.animeDao()
    @Provides fun provideEpisodeDao(db: AnimeDatabase): EpisodeDao = db.episodeDao()
    @Provides fun provideWatchHistoryDao(db: AnimeDatabase): WatchHistoryDao = db.watchHistoryDao()
    @Provides fun provideFavoriteDao(db: AnimeDatabase): FavoriteDao = db.favoriteDao()
    @Provides fun provideWatchlistDao(db: AnimeDatabase): WatchlistDao = db.watchlistDao()
    @Provides fun provideNotificationTrackDao(db: AnimeDatabase): NotificationTrackDao = db.notificationTrackDao()
}
