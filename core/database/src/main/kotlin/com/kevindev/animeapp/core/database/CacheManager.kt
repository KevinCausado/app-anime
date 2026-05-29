package com.kevindev.animeapp.core.database

import com.kevindev.animeapp.core.database.dao.AnimeDao
import javax.inject.Inject
import javax.inject.Singleton

private const val TTL_MS = 24 * 60 * 60 * 1000L // 24 horas

@Singleton
class CacheManager @Inject constructor(
    private val animeDao: AnimeDao,
) {
    suspend fun evictStale() {
        val cutoff = System.currentTimeMillis() - TTL_MS
        animeDao.deleteStaleAnime(cutoff)
    }

    fun isStale(cachedAt: Long): Boolean =
        System.currentTimeMillis() - cachedAt > TTL_MS
}
