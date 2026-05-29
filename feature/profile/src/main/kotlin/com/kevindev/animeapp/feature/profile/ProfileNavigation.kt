package com.kevindev.animeapp.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val PROFILE_SELECTION_ROUTE = "profile_selection"
const val PROFILE_EDIT_ROUTE = "profile_edit"

fun NavGraphBuilder.profileSelectionScreen(
    onProfileSelected: () -> Unit,
    onAddProfile: () -> Unit,
) {
    composable(PROFILE_SELECTION_ROUTE) {
        ProfileSelectionScreen(
            onProfileSelected = onProfileSelected,
            onAddProfile = onAddProfile,
        )
    }
}

fun NavGraphBuilder.profileEditScreen(
    onBack: () -> Unit,
) {
    composable(PROFILE_EDIT_ROUTE) {
        ProfileEditScreen(onBack = onBack)
    }
}

fun NavController.navigateToProfileSelection() = navigate(PROFILE_SELECTION_ROUTE)
fun NavController.navigateToProfileEdit() = navigate(PROFILE_EDIT_ROUTE)
