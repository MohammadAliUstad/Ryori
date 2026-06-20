package com.yugentech.ryori.di.module

import com.yugentech.ryori.api.cache.ApiCache
import com.yugentech.ryori.api.cache.CachedApi
import com.yugentech.ryori.api.repository.FoodRepository
import com.yugentech.ryori.api.repository.FoodRepositoryImpl
import com.yugentech.ryori.api.repository.RecipeRepository
import com.yugentech.ryori.api.repository.RecipeRepositoryImpl
import com.yugentech.ryori.api.service.DrinkService
import com.yugentech.ryori.api.service.FoodService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import org.koin.dsl.module

// Networking + data. View models live in viewModelModule.
val foodModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
    single {
        HttpClient {
            // INFO rather than BODY: recipe responses are large and Explore fires several
            // requests at once, so logging full bodies slows things down noticeably.
            install(Logging) { level = LogLevel.INFO }
        }
    }
    // Memory + Room response cache in front of every API call (see CachedApi / CachePolicy).
    single { ApiCache(dao = get()) }
    single { CachedApi(client = get(), cache = get(), json = get()) }

    single { FoodService(api = get()) }
    single { DrinkService(api = get()) }
    single<FoodRepository> { FoodRepositoryImpl(service = get()) }
    single<RecipeRepository> { RecipeRepositoryImpl(foodService = get(), drinkService = get(), settings = get()) }
}
