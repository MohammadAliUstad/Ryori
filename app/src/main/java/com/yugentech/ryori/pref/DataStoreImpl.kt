package com.yugentech.ryori.pref

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreImpl(private val context: Context) : DataStore {
    private val themeKey = stringPreferencesKey("theme")

    override suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[this@DataStoreImpl.themeKey] = theme
        }
    }

    override fun getTheme(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[this@DataStoreImpl.themeKey] ?: "Light"
        }
    }
}