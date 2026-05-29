package com.kevindev.animeapp.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevindev.animeapp.core.database.dao.ProfileDao
import com.kevindev.animeapp.core.database.entity.ProfileEntity
import com.kevindev.animeapp.core.datastore.ProfileDataStore
import com.kevindev.animeapp.core.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val profiles: List<Profile>,
        val activeProfileId: Long?,
    ) : ProfileUiState
}

data class ProfileEditState(
    val name: String = "",
    val isCreating: Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDao: ProfileDao,
    private val profileDataStore: ProfileDataStore,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        profileDao.getAllProfiles(),
        profileDataStore.activeProfileId,
    ) { entities, activeId ->
        ProfileUiState.Success(
            profiles = entities.map { it.toProfile() },
            activeProfileId = activeId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState.Loading)

    val editState = MutableStateFlow(ProfileEditState())

    fun selectProfile(profileId: Long) {
        viewModelScope.launch {
            profileDataStore.setActiveProfileId(profileId)
        }
    }

    fun setEditName(name: String) {
        editState.update { it.copy(name = name) }
    }

    fun createProfile(onDone: () -> Unit) {
        val name = editState.value.name.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            editState.update { it.copy(isCreating = true) }
            val id = profileDao.insertProfile(
                ProfileEntity(
                    name = name,
                    avatarPath = null,
                    createdAt = System.currentTimeMillis(),
                    isDefault = profileDao.getProfileCount() == 0,
                )
            )
            profileDataStore.setActiveProfileId(id)
            editState.update { ProfileEditState() }
            onDone()
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch {
            profileDao.deleteProfile(profile.toEntity())
            val current = (uiState.value as? ProfileUiState.Success)?.activeProfileId
            if (current == profile.id) {
                profileDataStore.clearActiveProfileId()
            }
        }
    }

    private fun ProfileEntity.toProfile() = Profile(
        id = id,
        name = name,
        avatarPath = avatarPath,
        createdAt = createdAt,
        isDefault = isDefault,
    )

    private fun Profile.toEntity() = ProfileEntity(
        id = id,
        name = name,
        avatarPath = avatarPath,
        createdAt = createdAt,
        isDefault = isDefault,
    )
}
