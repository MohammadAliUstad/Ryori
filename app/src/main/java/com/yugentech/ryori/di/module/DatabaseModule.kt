package com.yugentech.ryori.di.module

import androidx.room.Room
import com.yugentech.ryori.data.local.RyoriDatabase
import com.yugentech.ryori.data.settings.SettingsRepository
import com.yugentech.ryori.data.stats.KitchenRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

// Local storage: the Room database (API cache, recently viewed, kitchen stats) and the
// user's profile / preferences in DataStore.
val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), RyoriDatabase::class.java, RyoriDatabase.NAME)
            // Everything in here is either re-downloadable or a casual stat, so a schema
            // change can safely start from an empty database instead of needing migrations.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<RyoriDatabase>().apiCacheDao() }
    single { get<RyoriDatabase>().recentRecipeDao() }
    single { get<RyoriDatabase>().kitchenStatsDao() }

    single { SettingsRepository(dataStore = get(named("settings"))) }
    single { KitchenRepository(recentDao = get(), statsDao = get()) }
}
