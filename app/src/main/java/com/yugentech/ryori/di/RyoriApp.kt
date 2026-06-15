package com.yugentech.ryori.di

import android.app.Application
import com.yugentech.ryori.di.module.dataStoreModule
import com.yugentech.ryori.di.module.databaseModule
import com.yugentech.ryori.di.module.foodModule
import com.yugentech.ryori.di.module.themeModule
import com.yugentech.ryori.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RyoriApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@RyoriApp)
            modules(
                dataStoreModule,
                databaseModule,
                foodModule,
                themeModule,
                viewModelModule
            )
        }
    }
}