package com.yugentech.ryori.di.module

import com.yugentech.ryori.theme.datastore.ThemeDataStore
import com.yugentech.ryori.theme.repository.ThemeRepository
import com.yugentech.ryori.theme.repository.ThemeRepositoryImpl
import com.yugentech.ryori.theme.viewmodel.ThemeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Koin module defining dependencies for UI theming (same setup as Sessions)
val themeModule = module {

    // Service that saves and retrieves theme preferences
    single {
        ThemeDataStore(
            dataStore = get(named("theme"))
        )
    }

    // Repository acting as the single source of truth for the app's current theme. Its own
    // scope keeps it reading DataStore for the app's whole life, whatever screen is open.
    single<ThemeRepository> {
        ThemeRepositoryImpl(
            dataStore = get(),
            externalScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }

    // ViewModel for managing theme selection logic in the UI
    viewModel {
        ThemeViewModel(
            repository = get(),
            settingsRepository = get()
        )
    }
}
