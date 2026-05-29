package com.kevindev.animeapp.tv

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kevindev.animeapp.feature.detail.navigateToDetail
import com.kevindev.animeapp.feature.player.navigateToPlayer
import com.kevindev.animeapp.feature.profile.PROFILE_EDIT_ROUTE
import com.kevindev.animeapp.feature.profile.PROFILE_SELECTION_ROUTE
import com.kevindev.animeapp.feature.profile.ProfileUiState
import com.kevindev.animeapp.feature.profile.ProfileViewModel
import com.kevindev.animeapp.feature.profile.profileEditScreen
import com.kevindev.animeapp.feature.profile.profileSelectionScreen
import com.kevindev.animeapp.tv.screens.TvDetailScreen
import com.kevindev.animeapp.tv.screens.TvHomeScreen
import com.kevindev.animeapp.tv.screens.TvPlayerScreen
import com.kevindev.animeapp.tv.screens.TvSearchScreen
import java.net.URLDecoder

private const val TV_HOME_ROUTE = "tv_home"
private const val TV_SEARCH_ROUTE = "tv_search"

@Composable
fun TvNavHost() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val uiState by profileViewModel.uiState.collectAsState()

    val startDestination = when (val s = uiState) {
        is ProfileUiState.Loading -> return
        is ProfileUiState.Success -> if (s.activeProfileId != null) TV_HOME_ROUTE else PROFILE_SELECTION_ROUTE
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        profileSelectionScreen(
            onProfileSelected = {
                navController.navigate(TV_HOME_ROUTE) {
                    popUpTo(PROFILE_SELECTION_ROUTE) { inclusive = true }
                }
            },
            onAddProfile = { navController.navigate(PROFILE_EDIT_ROUTE) },
        )
        profileEditScreen(
            onBack = { navController.popBackStack() },
        )

        composable(TV_HOME_ROUTE) {
            TvHomeScreen(
                onAnimeClick = { animeId -> navController.navigateToDetail(animeId) },
            )
        }

        composable(TV_SEARCH_ROUTE) {
            TvSearchScreen(
                onAnimeClick = { animeId -> navController.navigateToDetail(animeId) },
            )
        }

        composable(
            route = "detail/{animeId}",
            arguments = listOf(navArgument("animeId") { type = NavType.IntType }),
        ) { backStack ->
            val animeId = backStack.arguments!!.getInt("animeId")
            TvDetailScreen(
                animeId = animeId,
                onBack = { navController.popBackStack() },
                onPlayEpisode = { id, episodeId -> navController.navigateToPlayer(id, episodeId) },
                onAnimeClick = { navController.navigateToDetail(it) },
            )
        }

        composable(
            route = "player/{animeId}/{episodeId}",
            arguments = listOf(
                navArgument("animeId") { type = NavType.IntType },
                navArgument("episodeId") { type = NavType.StringType },
            ),
        ) { backStack ->
            val animeId = backStack.arguments!!.getInt("animeId")
            val episodeId = URLDecoder.decode(backStack.arguments!!.getString("episodeId")!!, "UTF-8")
            TvPlayerScreen(
                animeId = animeId,
                episodeId = episodeId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
