package com.kevindev.animeapp.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "anime_prefs")

@Singleton
class ProfileDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val activeProfileIdKey = longPreferencesKey("active_profile_id")

    val activeProfileId: Flow<Long?> = context.dataStore.data
        .map { prefs -> prefs[activeProfileIdKey] }

    suspend fun setActiveProfileId(id: Long) {
        context.dataStore.edit { prefs ->
            prefs[activeProfileIdKey] = id
        }
    }

    suspend fun clearActiveProfileId() {
        context.dataStore.edit { prefs ->
            prefs.remove(activeProfileIdKey)
        }
    }
}
