package com.kevindev.animeapp.feature.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevindev.animeapp.core.network.graphql.type.MediaFormat
import com.kevindev.animeapp.core.network.graphql.type.MediaStatus
import com.kevindev.animeapp.core.ui.theme.AnimeSurface
import com.kevindev.animeapp.core.ui.theme.AnimeSurfaceVariant
import com.kevindev.animeapp.core.ui.theme.AnimeOrange
import com.kevindev.animeapp.feature.search.SearchFilters

private val GENRES = listOf(
    "Action", "Adventure", "Comedy", "Drama", "Fantasy",
    "Horror", "Mystery", "Romance", "Sci-Fi", "Slice of Life",
    "Sports", "Supernatural", "Thriller",
)

private val FORMATS = listOf(
    MediaFormat.TV to "TV",
    MediaFormat.MOVIE to "Película",
    MediaFormat.OVA to "OVA",
    MediaFormat.ONA to "ONA",
    MediaFormat.SPECIAL to "Especial",
)

private val STATUSES = listOf(
    MediaStatus.RELEASING to "En emisión",
    MediaStatus.FINISHED to "Finalizado",
    MediaStatus.NOT_YET_RELEASED to "Próximamente",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterSheet(
    current: SearchFilters,
    onApply: (SearchFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var genre by remember { mutableStateOf(current.genre) }
    var status by remember { mutableStateOf(current.status) }
    var format by remember { mutableStateOf(current.format) }

    ModalBottomSheet(
        onDismissRequest = {
            onApply(SearchFilters(genre = genre, status = status, format = format))
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AnimeSurface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
        ) {
            FilterSection(title = "Género") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GENRES.forEach { g ->
                        FilterChip(
                            selected = genre == g,
                            onClick = { genre = if (genre == g) null else g },
                            label = { Text(g, fontSize = 12.sp) },
                            colors = chipColors(),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilterSection(title = "Formato") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FORMATS.forEach { (f, label) ->
                        FilterChip(
                            selected = format == f,
                            onClick = { format = if (format == f) null else f },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = chipColors(),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FilterSection(title = "Estado") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    STATUSES.forEach { (s, label) ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = if (status == s) null else s },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = chipColors(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 10.dp),
    )
    content()
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = AnimeOrange,
    selectedLabelColor = Color.White,
    containerColor = AnimeSurfaceVariant,
    labelColor = Color.Gray,
)
