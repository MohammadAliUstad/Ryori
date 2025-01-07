package com.example.ryori.pref

import kotlinx.coroutines.flow.Flow

interface DataStore {
    suspend fun saveTheme(theme: String)

    fun getTheme(): Flow<String>
}