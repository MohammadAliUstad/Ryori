package com.yugentech.ryori.di

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.yugentech.ryori.di.module.dataStoreModule
import com.yugentech.ryori.di.module.databaseModule
import com.yugentech.ryori.di.module.foodModule
import com.yugentech.ryori.di.module.themeModule
import com.yugentech.ryori.di.module.viewModelModule
import com.yugentech.ryori.localization.AppLocale
import com.yugentech.ryori.localization.AppStrings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RyoriApp : Application(), ImageLoaderFactory {

    // Below Android 13, the language picked in the app (see AppLocale), so anything read from
    // the app context follows it too; from 13 the system applies it
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(AppLocale.wrap(base))
    }

    override fun onCreate() {
        super.onCreate()
        AppStrings.init(this)

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

    // App-wide image loader for every AsyncImage: photos fade in when they arrive instead of
    // snapping on. Images already in the memory cache still show instantly.
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .crossfade(IMAGE_CROSSFADE_MILLIS)
            .build()

    private companion object {
        const val IMAGE_CROSSFADE_MILLIS = 400
    }
}
