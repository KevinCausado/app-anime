package com.kevindev.animeapp.tv.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import coil3.compose.AsyncImage
import com.kevindev.animeapp.core.ui.components.ErrorState
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.core.ui.theme.AnimeOrange
import com.kevindev.animeapp.core.ui.theme.AnimeOnSurfaceMuted
import com.kevindev.animeapp.core.ui.theme.AnimeSurface
import com.kevindev.animeapp.core.uitv.components.TvSectionRow
import com.kevindev.animeapp.feature.detail.DetailUiState
import com.kevindev.animeapp.feature.detail.DetailViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvDetailScreen(
    animeId: Int,
    onBack: () -> Unit,
    onPlayEpisode: (animeId: Int, episodeId: String) -> Unit,
    onAnimeClick: (Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeBg),
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Text("Cargando...", color = Color.Gray, fontSize = 18.sp, modifier = Modifier.align(Alignment.Center))
            }
            is DetailUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is DetailUiState.Success -> TvDetailContent(
                state = state,
                animeId = animeId,
                onPlayEpisode = onPlayEpisode,
                onAnimeClick = onAnimeClick,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvDetailContent(
    state: DetailUiState.Success,
    animeId: Int,
    onPlayEpisode: (animeId: Int, episodeId: String) -> Unit,
    onAnimeClick: (Int) -> Unit,
) {
    val firstEpisodeId = state.episodes.firstOrNull()?.id
    val resumeEpisodeId = state.episodes.find { it.number == state.lastWatchedEpisode }?.id ?: firstEpisodeId

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            // Banner + info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(21f / 9f),
            ) {
                AsyncImage(
                    model = state.anime.bannerImageUrl ?: state.anime.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(AnimeBg, Color.Transparent),
                                endX = 900f,
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 48.dp)
                        .fillMaxWidth(0.45f),
                ) {
                    Text(
                        text = state.anime.titleRomaji,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (state.anime.averageScore != null) {
                        Text(
                            text = "★ ${state.anime.averageScore / 10.0}",
                            color = AnimeOrange,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    if (state.anime.description != null) {
                        Text(
                            text = state.anime.description,
                            color = AnimeOnSurfaceMuted,
                            fontSize = 13.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val id = resumeEpisodeId ?: return@Button
                            onPlayEpisode(animeId, id)
                        },
                        colors = ButtonDefaults.colors(containerColor = AnimeOrange),
                        shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.lastWatchedEpisode != null) "Continuar Ep. ${state.lastWatchedEpisode}" else "Reproducir",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        if (state.episodes.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                Text(
                    text = "Episodios (${state.episodes.size})",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 48.dp),
                )
            }
            items(state.episodes.take(5), key = { it.id }) { episode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = { onPlayEpisode(animeId, episode.id) },
                        colors = ButtonDefaults.colors(
                            containerColor = AnimeSurface,
                        ),
                        shape = ButtonDefaults.shape(shape = RoundedCornerShape(6.dp)),
                    ) {
                        Text(
                            text = "Ep. ${episode.number}${if (episode.title != null) " — ${episode.title}" else ""}",
                            color = Color.White,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        if (state.recommendations.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                TvSectionRow(
                    title = "Recomendados",
                    animes = state.recommendations,
                    onAnimeClick = onAnimeClick,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}
