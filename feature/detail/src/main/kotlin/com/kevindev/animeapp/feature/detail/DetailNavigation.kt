package com.kevindev.animeapp.feature.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val DETAIL_ROUTE = "detail/{animeId}"

fun NavGraphBuilder.detailScreen(
    onBack: () -> Unit,
    onPlayEpisode: (animeId: Int, episodeId: String) -> Unit,
) {
    composable(
        route = DETAIL_ROUTE,
        arguments = listOf(navArgument("animeId") { type = NavType.IntType }),
    ) { backStack ->
        val animeId = backStack.arguments!!.getInt("animeId")
        DetailScreen(
            animeId = animeId,
            onBack = onBack,
            onPlayEpisode = onPlayEpisode,
        )
    }
}

fun NavController.navigateToDetail(animeId: Int) = navigate("detail/$animeId")
