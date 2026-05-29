package com.kevindev.animeapp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevindev.animeapp.core.model.Anime

@Composable
fun AnimeSectionRow(
    title: String,
    animes: List<Anime>,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = title, onSeeAllClick = onSeeAllClick)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(animes, key = { it.id }) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = { onAnimeClick(anime.id) },
                )
            }
        }
    }
}
