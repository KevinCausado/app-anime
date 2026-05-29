package com.kevindev.animeapp.feature.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SEARCH_ROUTE = "search"

fun NavGraphBuilder.searchScreen(
    onAnimeClick: (Int) -> Unit,
) {
    composable(SEARCH_ROUTE) {
        SearchScreen(onAnimeClick = onAnimeClick)
    }
}

fun NavController.navigateToSearch() = navigate(SEARCH_ROUTE)
