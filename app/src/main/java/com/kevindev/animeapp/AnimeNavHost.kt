package com.kevindev.animeapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kevindev.animeapp.feature.detail.detailScreen
import com.kevindev.animeapp.feature.detail.navigateToDetail
import com.kevindev.animeapp.feature.home.HOME_ROUTE
import com.kevindev.animeapp.feature.home.homeScreen
import com.kevindev.animeapp.feature.profile.PROFILE_EDIT_ROUTE
import com.kevindev.animeapp.feature.profile.PROFILE_SELECTION_ROUTE
import com.kevindev.animeapp.feature.profile.ProfileUiState
import com.kevindev.animeapp.feature.profile.ProfileViewModel
import com.kevindev.animeapp.feature.profile.profileEditScreen
import com.kevindev.animeapp.feature.profile.profileSelectionScreen
import com.kevindev.animeapp.feature.search.searchScreen

@Composable
fun AnimeNavHost() {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val uiState by profileViewModel.uiState.collectAsState()

    val startDestination = when (val s = uiState) {
        is ProfileUiState.Loading -> return
        is ProfileUiState.Success -> if (s.activeProfileId != null) HOME_ROUTE else PROFILE_SELECTION_ROUTE
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        profileSelectionScreen(
            onProfileSelected = {
                navController.navigate(HOME_ROUTE) {
                    popUpTo(PROFILE_SELECTION_ROUTE) { inclusive = true }
                }
            },
            onAddProfile = {
                navController.navigate(PROFILE_EDIT_ROUTE)
            },
        )
        profileEditScreen(
            onBack = { navController.popBackStack() },
        )
        homeScreen(
            onAnimeClick = { animeId -> navController.navigateToDetail(animeId) },
        )
        searchScreen(
            onAnimeClick = { animeId -> navController.navigateToDetail(animeId) },
        )
        detailScreen(
            onBack = { navController.popBackStack() },
            onPlayEpisode = { _, _ -> /* player en Fase 9 */ },
        )
        // Rutas de features siguientes se agregan aquí
    }
}
