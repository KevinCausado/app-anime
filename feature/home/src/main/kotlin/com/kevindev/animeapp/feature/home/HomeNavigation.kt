package com.kevindev.animeapp.feature.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val HOME_ROUTE = "home"

fun NavGraphBuilder.homeScreen(
    onAnimeClick: (Int) -> Unit,
) {
    composable(HOME_ROUTE) {
        HomeScreen(onAnimeClick = onAnimeClick)
    }
}
