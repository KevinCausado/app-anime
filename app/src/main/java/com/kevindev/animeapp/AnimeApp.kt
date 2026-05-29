package com.kevindev.animeapp

import android.app.Application
import androidx.work.Configuration
import com.kevindev.animeapp.notification.NotificationHelper
import com.kevindev.animeapp.notification.scheduleNewEpisodeWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AnimeApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerConfig: Configuration

    override val workManagerConfiguration: Configuration
        get() = workerConfig

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
        scheduleNewEpisodeWorker(this)
    }
}
