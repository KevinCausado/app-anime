package com.kevindev.animeapp.feature.search

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kevindev.animeapp.core.ui.components.AnimeCard
import com.kevindev.animeapp.core.ui.components.AnimeCardShimmer
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.core.ui.theme.AnimeOrange
import com.kevindev.animeapp.feature.search.components.AnimeSearchBar
import com.kevindev.animeapp.feature.search.components.FilterSheet

@Composable
fun SearchScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val results = viewModel.results.collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeBg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimeSearchBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                onClear = viewModel::clearQuery,
                onFilterClick = viewModel::toggleFilters,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            when (val refresh = results.loadState.refresh) {
                is LoadState.Loading -> ShimmerGrid()
                is LoadState.Error -> ErrorGrid(
                    message = refresh.error.message ?: "Error al buscar",
                    onRetry = { results.retry() },
                )
                is LoadState.NotLoading -> {
                    if (results.itemCount == 0 && uiState.query.length >= 3) {
                        EmptyResults()
                    } else {
                        ResultsGrid(
                            results = results,
                            onAnimeClick = onAnimeClick,
                        )
                    }
                }
            }
        }

        if (uiState.showFilters) {
            FilterSheet(
                current = uiState.filters,
                onApply = viewModel::onFiltersChange,
                onDismiss = viewModel::toggleFilters,
            )
        }
    }
}

@Composable
private fun ResultsGrid(
    results: androidx.paging.compose.LazyPagingItems<com.kevindev.animeapp.core.model.Anime>,
    onAnimeClick: (Int) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            count = results.itemCount,
            key = results.itemKey { it.id },
        ) { index ->
            val anime = results[index]
            if (anime != null) {
                AnimeCard(anime = anime, onClick = { onAnimeClick(anime.id) })
            }
        }

        if (results.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AnimeOrange)
                }
            }
        }
    }
}

@Composable
private fun ShimmerGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(12) { AnimeCardShimmer() }
    }
}

@Composable
private fun EmptyResults() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Sin resultados",
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ErrorGrid(message: String, onRetry: () -> Unit) {
    com.kevindev.animeapp.core.ui.components.ErrorState(
        message = message,
        onRetry = onRetry,
    )
}
