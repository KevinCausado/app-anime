package com.kevindev.animeapp.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevindev.animeapp.core.ui.components.AnimeSectionRow
import com.kevindev.animeapp.core.ui.components.ErrorState
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.feature.detail.components.DetailHeader
import com.kevindev.animeapp.feature.detail.components.EpisodeList
import com.kevindev.animeapp.feature.detail.components.RelatedAnimeRow

@Composable
fun DetailScreen(
    animeId: Int,
    onBack: () -> Unit,
    onPlayEpisode: (animeId: Int, episodeId: String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeBg),
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> DetailLoading(onBack = onBack)
            is DetailUiState.Error -> ErrorState(
                message = state.message,
                onRetry = viewModel::load,
            )
            is DetailUiState.Success -> DetailContent(
                state = state,
                onBack = onBack,
                onPlayEpisode = { episodeId -> onPlayEpisode(animeId, episodeId) },
                onFavoriteClick = viewModel::toggleFavorite,
                onWatchlistClick = viewModel::toggleWatchlist,
                onRelatedClick = { /* navegar a detail de otro anime — se conecta desde NavHost */ },
            )
        }
    }
}

@Composable
private fun DetailContent(
    state: DetailUiState.Success,
    onBack: () -> Unit,
    onPlayEpisode: (episodeId: String) -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchlistClick: () -> Unit,
    onRelatedClick: (Int) -> Unit,
) {
    val firstEpisodeId = state.episodes.firstOrNull()?.id
    val resumeEpisode = state.lastWatchedEpisode
    val resumeEpisodeId = state.episodes
        .find { it.number == resumeEpisode }?.id ?: firstEpisodeId

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            DetailHeader(
                anime = state.anime,
                isFavorite = state.isFavorite,
                isInWatchlist = state.isInWatchlist,
                resumeEpisode = resumeEpisode,
                onBack = onBack,
                onFavoriteClick = onFavoriteClick,
                onWatchlistClick = onWatchlistClick,
                onPlayClick = {
                    val id = resumeEpisodeId ?: return@DetailHeader
                    onPlayEpisode(id)
                },
            )
        }

        if (state.episodes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                EpisodeList(
                    episodes = state.episodes,
                    progressMap = emptyMap(),
                    onEpisodeClick = { episode -> onPlayEpisode(episode.id) },
                )
            }
        }

        if (state.related.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                RelatedAnimeRow(
                    title = "Relacionados",
                    items = state.related,
                    onAnimeClick = onRelatedClick,
                )
            }
        }

        if (state.recommendations.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                AnimeSectionRow(
                    title = "Recomendados",
                    animes = state.recommendations,
                    onAnimeClick = onRelatedClick,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun DetailLoading(onBack: () -> Unit) {
    // Banner placeholder + shimmer mínimo
    com.kevindev.animeapp.core.ui.components.AnimeSectionRowShimmer()
}
