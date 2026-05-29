package com.kevindev.animeapp.feature.player

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder

const val PLAYER_ROUTE = "player/{animeId}/{episodeId}"

fun NavGraphBuilder.playerScreen(
    onBack: () -> Unit,
) {
    composable(
        route = PLAYER_ROUTE,
        arguments = listOf(
            navArgument("animeId") { type = NavType.IntType },
            navArgument("episodeId") { type = NavType.StringType },
        ),
    ) { backStack ->
        val animeId = backStack.arguments!!.getInt("animeId")
        val episodeId = URLDecoder.decode(backStack.arguments!!.getString("episodeId")!!, "UTF-8")
        PlayerScreen(
            animeId = animeId,
            episodeId = episodeId,
            onBack = onBack,
        )
    }
}

fun NavController.navigateToPlayer(animeId: Int, episodeId: String) {
    val encoded = URLEncoder.encode(episodeId, "UTF-8")
    navigate("player/$animeId/$encoded")
}
