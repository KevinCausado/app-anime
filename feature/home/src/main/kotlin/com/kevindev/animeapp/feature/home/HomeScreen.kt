package com.kevindev.animeapp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kevindev.animeapp.core.ui.components.AnimeSectionRow
import com.kevindev.animeapp.core.ui.components.AnimeSectionRowShimmer
import com.kevindev.animeapp.core.ui.components.ErrorState
import com.kevindev.animeapp.core.ui.components.SectionHeader
import com.kevindev.animeapp.core.ui.components.ShimmerBox
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.feature.home.components.ContinueWatchingCard
import com.kevindev.animeapp.feature.home.components.HeroBanner

@Composable
fun HomeScreen(
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
            is HomeUiState.Loading -> HomeLoading()
            is HomeUiState.Error -> ErrorState(
                message = state.message,
                onRetry = viewModel::load,
            )
            is HomeUiState.Success -> HomeContent(
                state = state,
                onAnimeClick = onAnimeClick,
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onAnimeClick: (Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HeroBanner(
                animes = state.hero,
                onAnimeClick = onAnimeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
            )
        }

        if (state.continueWatching.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Continuar viendo")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    items(state.continueWatching, key = { "${it.anime.id}_${it.episodeNumber}" }) { item ->
                        ContinueWatchingCard(
                            item = item,
                            onClick = { onAnimeClick(item.anime.id) },
                        )
                    }
                }
            }
        }

        items(state.sections, key = { it.title }) { section ->
            Spacer(modifier = Modifier.height(24.dp))
            AnimeSectionRow(
                title = section.title,
                animes = section.items,
                onAnimeClick = onAnimeClick,
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun HomeLoading() {
    Column(modifier = Modifier.fillMaxSize()) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
        )
        repeat(2) {
            Spacer(modifier = Modifier.height(24.dp))
            AnimeSectionRowShimmer()
        }
    }
}
