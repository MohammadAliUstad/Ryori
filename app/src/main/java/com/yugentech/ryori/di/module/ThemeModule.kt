package com.yugentech.ryori.di.module

import com.yugentech.ryori.theme.datastore.ThemeDataStore
import com.yugentech.ryori.theme.repository.ThemeRepository
import com.yugentech.ryori.theme.repository.ThemeRepositoryImpl
import com.yugentech.ryori.theme.viewmodel.ThemeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val themeModule = module {

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
            repository = get(),
            settingsRepository = get()
        )
    }
}
