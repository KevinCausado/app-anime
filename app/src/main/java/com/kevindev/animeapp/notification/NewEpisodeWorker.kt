package com.kevindev.animeapp.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kevindev.animeapp.core.database.dao.AnimeDao
import com.kevindev.animeapp.core.database.dao.NotificationTrackDao
import com.kevindev.animeapp.core.database.entity.NotificationTrackEntity
import com.kevindev.animeapp.core.network.consumet.HiAnimeApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class NewEpisodeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationTrackDao: NotificationTrackDao,
    private val animeDao: AnimeDao,
    private val hiAnimeApi: HiAnimeApi,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val tracked = notificationTrackDao.getAll()
            if (tracked.isEmpty()) return Result.success()

            for (track in tracked) {
                checkAnime(track)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun checkAnime(track: NotificationTrackEntity) {
        try {
            val searchResult = hiAnimeApi.search(query = track.animeAnilistId.toString())
            val consumetId = searchResult.results.firstOrNull()?.id ?: return
            val episodesResponse = hiAnimeApi.getEpisodes(consumetId)
            val latestEpisode = episodesResponse.episodes.maxOfOrNull { it.number } ?: return

            if (latestEpisode > track.lastCheckedEpisode) {
                val animeTitle = animeDao.getAnimeById(track.animeAnilistId)
                    .firstOrNull()?.titleRomaji ?: "Anime"

                NotificationHelper.showNewEpisode(
                    context = applicationContext,
                    animeTitle = animeTitle,
                    episodeNumber = latestEpisode,
                    animeId = track.animeAnilistId,
                )

                notificationTrackDao.upsert(
                    track.copy(
                        lastCheckedEpisode = latestEpisode,
                        lastCheckedAt = System.currentTimeMillis(),
                    )
                )
            }
        } catch (_: Exception) {
            // Si falla un anime individual, continúa con el siguiente
        }
    }
}
