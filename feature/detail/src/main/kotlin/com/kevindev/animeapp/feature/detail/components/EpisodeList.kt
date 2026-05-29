package com.kevindev.animeapp.feature.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevindev.animeapp.core.model.Episode
import com.kevindev.animeapp.core.ui.components.EpisodeCard

@Composable
fun EpisodeList(
    episodes: List<Episode>,
    progressMap: Map<Int, Float>,
    onEpisodeClick: (Episode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Episodios (${episodes.size})",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            episodes.forEach { episode ->
                EpisodeCard(
                    episode = episode,
                    progress = progressMap[episode.number] ?: 0f,
                    onClick = { onEpisodeClick(episode) },
                )
            }
        }
    }
}
