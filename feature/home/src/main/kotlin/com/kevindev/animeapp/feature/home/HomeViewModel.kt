package com.kevindev.animeapp.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.kevindev.animeapp.core.database.dao.AnimeDao
import com.kevindev.animeapp.core.database.dao.WatchHistoryDao
import com.kevindev.animeapp.core.database.entity.AnimeEntity
import com.kevindev.animeapp.core.datastore.ProfileDataStore
import com.kevindev.animeapp.core.model.Anime
import com.kevindev.animeapp.core.model.AnimeSeason
import com.kevindev.animeapp.core.model.AnimeStatus
import com.kevindev.animeapp.core.model.AnimeFormat
import com.kevindev.animeapp.core.network.apollo.toAnime
import com.kevindev.animeapp.core.network.graphql.SeasonalAnimeQuery
import com.kevindev.animeapp.core.network.graphql.TrendingAnimeQuery
import com.kevindev.animeapp.core.network.graphql.type.MediaSeason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import javax.inject.Inject

data class AnimeSection(
    val title: String,
    val items: List<Anime>,
)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val hero: List<Anime>,
        val sections: List<AnimeSection>,
        val continueWatching: List<ContinueWatchingItem>,
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

data class ContinueWatchingItem(
    val anime: Anime,
    val episodeNumber: Int,
    val progressFraction: Float,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apolloClient: ApolloClient,
    private val animeDao: AnimeDao,
    private val watchHistoryDao: WatchHistoryDao,
    private val profileDataStore: ProfileDataStore,
    private val json: Json,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val (season, year) = currentSeasonAndYear()

                val trendingDeferred = async { fetchTrending() }
                val seasonalDeferred = async { fetchSeasonal(season, year) }

                val trending = trendingDeferred.await()
                val seasonal = seasonalDeferred.await()

                cacheAnimes(trending + seasonal)

                val profileId = profileDataStore.activeProfileId.firstOrNull()
                val continueWatching = if (profileId != null) {
                    buildContinueWatching(profileId, trending + seasonal)
                } else emptyList()

                val sections = buildList {
                    if (trending.isNotEmpty()) add(AnimeSection("Tendencia", trending))
                    if (seasonal.isNotEmpty()) add(AnimeSection("Esta temporada", seasonal))
                }

                _uiState.value = HomeUiState.Success(
                    hero = trending.take(5),
                    sections = sections,
                    continueWatching = continueWatching,
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private suspend fun fetchTrending(): List<Anime> {
        val response = apolloClient.query(TrendingAnimeQuery(page = 1, perPage = 20)).execute()
        return response.data?.Page?.media?.mapNotNull { it?.animeFragment?.toAnime() } ?: emptyList()
    }

    private suspend fun fetchSeasonal(season: MediaSeason, year: Int): List<Anime> {
        val response = apolloClient.query(
            SeasonalAnimeQuery(
                season = com.apollographql.apollo.api.Optional.present(season),
                year = com.apollographql.apollo.api.Optional.present(year),
                page = 1,
                perPage = 20,
            )
        ).execute()
        return response.data?.Page?.media?.mapNotNull { it?.animeFragment?.toAnime() } ?: emptyList()
    }

    private suspend fun buildContinueWatching(
        profileId: Long,
        knownAnimes: List<Anime>,
    ): List<ContinueWatchingItem> {
        val history = watchHistoryDao.getRecentHistory(profileId, limit = 10).firstOrNull() ?: return emptyList()
        val inProgress = history.filter { it.completedAt == null && it.progressMs > 0 }
        val animeMap = knownAnimes.associateBy { it.id }
        return inProgress.mapNotNull { entry ->
            val anime = animeMap[entry.animeAnilistId] ?: return@mapNotNull null
            ContinueWatchingItem(
                anime = anime,
                episodeNumber = entry.episodeNumber,
                progressFraction = if (entry.durationMs > 0)
                    (entry.progressMs.toFloat() / entry.durationMs).coerceIn(0f, 1f)
                else 0f,
            )
        }
    }

    private suspend fun cacheAnimes(animes: List<Anime>) {
        val now = System.currentTimeMillis()
        animeDao.upsertAnimes(animes.map { it.toEntity(now) })
    }

    private fun currentSeasonAndYear(): Pair<MediaSeason, Int> {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        val season = when (month) {
            12, 1, 2 -> MediaSeason.WINTER
            3, 4, 5 -> MediaSeason.SPRING
            6, 7, 8 -> MediaSeason.SUMMER
            else -> MediaSeason.FALL
        }
        return season to year
    }

    private fun Anime.toEntity(cachedAt: Long) = AnimeEntity(
        anilistId = id,
        titleRomaji = titleRomaji,
        titleEnglish = titleEnglish,
        titleNative = titleNative,
        coverImageUrl = coverImageUrl,
        bannerImageUrl = bannerImageUrl,
        description = description,
        status = status.name,
        format = format.name,
        episodes = episodes,
        duration = duration,
        averageScore = averageScore,
        genres = json.encodeToString(genres),
        season = season?.name,
        seasonYear = seasonYear,
        studios = json.encodeToString(studios),
        nextAiringEpisode = nextAiringEpisode,
        nextAiringAt = nextAiringAt,
        isAdult = isAdult,
        cachedAt = cachedAt,
    )
}
