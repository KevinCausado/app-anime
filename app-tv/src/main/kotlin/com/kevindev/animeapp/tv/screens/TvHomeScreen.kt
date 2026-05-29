package com.kevindev.animeapp.tv.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Carousel
import androidx.tv.material3.CarouselDefaults
import androidx.tv.material3.CarouselState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil3.compose.AsyncImage
import com.kevindev.animeapp.core.ui.components.ErrorState
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.core.ui.theme.AnimeOrange
import com.kevindev.animeapp.core.uitv.components.TvSectionRow
import com.kevindev.animeapp.feature.home.HomeUiState
import com.kevindev.animeapp.feature.home.HomeViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvHomeScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeBg),
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> TvHomeLoading()
            is HomeUiState.Error -> ErrorState(message = state.message, onRetry = viewModel::load)
            is HomeUiState.Success -> TvHomeContent(state = state, onAnimeClick = onAnimeClick)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TvHomeContent(
    state: HomeUiState.Success,
    onAnimeClick: (Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (state.hero.isNotEmpty()) {
            item {
                Carousel(
                    itemCount = state.hero.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(21f / 9f),
                    carouselState = CarouselState(),
                ) { index ->
                    val anime = state.hero[index]
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = anime.bannerImageUrl ?: anime.coverImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, AnimeBg),
                                        startY = 300f,
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 48.dp, bottom = 40.dp),
                        ) {
                            Text(
                                text = anime.titleRomaji,
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            if (anime.averageScore != null) {
                                Text(
                                    text = "★ ${anime.averageScore / 10.0}",
                                    color = AnimeOrange,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        items(state.sections, key = { it.title }) { section ->
            Spacer(modifier = Modifier.height(32.dp))
            TvSectionRow(
                title = section.title,
                animes = section.items,
                onAnimeClick = onAnimeClick,
            )
        }

        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

@Composable
private fun TvHomeLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Cargando...", color = Color.Gray, fontSize = 18.sp)
    }
}
