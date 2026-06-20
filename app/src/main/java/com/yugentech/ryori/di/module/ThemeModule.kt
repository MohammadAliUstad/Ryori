package com.yugentech.ryori.di.module

import com.yugentech.ryori.theme.datastore.ThemeDataStore
import com.yugentech.ryori.theme.repository.ThemeRepository
import com.yugentech.ryori.theme.repository.ThemeRepositoryImpl
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.viewmodel.ThemeViewModel
import com.yugentech.ryori.data.settings.SettingsRepository
import kotlinx.coroutines.flow.map
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val themeModule = module {

    // Follows the "Haptic feedback" switch on the More screen.
    single {
        HapticService(
            context = androidContext(),
            hapticsEnabledFlow = get<SettingsRepository>().settings.map { it.hapticsEnabled }
        )
    }

    single {
        ThemeDataStore(
            dataStore = get(named("theme"))
        )
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(
            dataStore = get()
        )
    }

    viewModel {
        ThemeViewModel(
            repository = get()
        )
    }
}
