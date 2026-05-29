package com.kevindev.animeapp.tv.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kevindev.animeapp.core.ui.theme.AnimeBg
import com.kevindev.animeapp.core.ui.theme.AnimeOrange
import com.kevindev.animeapp.core.uitv.components.TvAnimeCard
import com.kevindev.animeapp.feature.search.SearchViewModel
import com.kevindev.animeapp.tv.components.TvSearchBar

@Composable
fun TvSearchScreen(
    onAnimeClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val results = viewModel.results.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeBg)
            .padding(horizontal = 48.dp, vertical = 24.dp),
    ) {
        TvSearchBar(
            query = uiState.query,
            onQueryChange = viewModel::onQueryChange,
            onClear = viewModel::clearQuery,
            modifier = Modifier.fillMaxWidth(),
        )

        Box(modifier = Modifier.weight(1f)) {
            when (val refresh = results.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(
                        color = AnimeOrange,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is LoadState.Error -> {
                    Text(
                        text = refresh.error.message ?: "Error",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is LoadState.NotLoading -> {
                    if (results.itemCount == 0 && uiState.query.length >= 3) {
                        Text(
                            text = "Sin resultados",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            contentPadding = PaddingValues(top = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(
                                count = results.itemCount,
                                key = results.itemKey { it.id },
                            ) { index ->
                                val anime = results[index]
                                if (anime != null) {
                                    TvAnimeCard(
                                        anime = anime,
                                        onClick = { onAnimeClick(anime.id) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
