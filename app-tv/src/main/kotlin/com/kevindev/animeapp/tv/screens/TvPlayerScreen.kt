package com.kevindev.animeapp.tv.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kevindev.animeapp.feature.player.PlayerScreen

// Reutiliza PlayerScreen de feature:player directamente.
// Los controles ya responden a eventos del D-pad via Compose focus system.
@Composable
fun TvPlayerScreen(
    animeId: Int,
    episodeId: String,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        PlayerScreen(
            animeId = animeId,
            episodeId = episodeId,
            onBack = onBack,
        )
    }
}
