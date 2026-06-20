package com.yugentech.ryori.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// The user's profile and preferences, stored on the device (no account needed).
data class UserSettings(
    val chefName: String = DEFAULT_CHEF_NAME,
    val avatarId: Int = DEFAULT_AVATAR_ID,
    val hapticsEnabled: Boolean = true,
    val vegetarianMode: Boolean = false,
    val keepScreenOn: Boolean = false
) {
    companion object {
        const val DEFAULT_CHEF_NAME = "Chef"
        const val DEFAULT_AVATAR_ID = 1
    }
}

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val CHEF_NAME = stringPreferencesKey("chef_name")
        val AVATAR_ID = intPreferencesKey("avatar_id")
        val HAPTICS = booleanPreferencesKey("haptics_enabled")
        val VEGETARIAN = booleanPreferencesKey("vegetarian_mode")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
    }

    val settings: Flow<UserSettings> = dataStore.data
        .map { prefs ->
            UserSettings(
                chefName = prefs[Keys.CHEF_NAME] ?: UserSettings.DEFAULT_CHEF_NAME,
                avatarId = prefs[Keys.AVATAR_ID] ?: UserSettings.DEFAULT_AVATAR_ID,
                hapticsEnabled = prefs[Keys.HAPTICS] ?: true,
                vegetarianMode = prefs[Keys.VEGETARIAN] ?: false,
                keepScreenOn = prefs[Keys.KEEP_SCREEN_ON] ?: false
            )
        }
        .distinctUntilChanged()

    val vegetarianMode: Flow<Boolean> = settings.map { it.vegetarianMode }.distinctUntilChanged()

    suspend fun current(): UserSettings = settings.first()

    suspend fun setChefName(name: String) {
        val clean = name.trim().take(MAX_NAME_LENGTH).ifEmpty { UserSettings.DEFAULT_CHEF_NAME }
        dataStore.edit { it[Keys.CHEF_NAME] = clean }
    }

    suspend fun setAvatar(id: Int) = dataStore.edit { it[Keys.AVATAR_ID] = id }

    suspend fun setHaptics(enabled: Boolean) = dataStore.edit { it[Keys.HAPTICS] = enabled }

    suspend fun setVegetarianMode(enabled: Boolean) = dataStore.edit { it[Keys.VEGETARIAN] = enabled }

    suspend fun setKeepScreenOn(enabled: Boolean) = dataStore.edit { it[Keys.KEEP_SCREEN_ON] = enabled }

    companion object {
        const val MAX_NAME_LENGTH = 24
    }
}
