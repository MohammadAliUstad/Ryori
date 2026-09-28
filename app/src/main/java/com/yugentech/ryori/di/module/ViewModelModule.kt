package com.yugentech.ryori.di.module

import com.yugentech.ryori.api.viewmodel.ExploreViewModel
import com.yugentech.ryori.api.viewmodel.HomeViewModel
import com.yugentech.ryori.api.viewmodel.MoreViewModel
import com.yugentech.ryori.api.viewmodel.RecentlyViewedViewModel
import com.yugentech.ryori.api.viewmodel.RecipeListViewModel
import com.yugentech.ryori.api.viewmodel.RecipeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        HomeViewModel(
            repository = get(),
            settings = get()
        )
    }

    viewModel {
        ExploreViewModel(
            repository = get(),
            settings = get()
        )
    }

    viewModel {
        RecipeViewModel(
            repository = get(),
            kitchen = get(),
            settings = get()
        )
    }

    viewModel {
        RecipeListViewModel(
            repository = get()
        )
    }

    viewModel {
        MoreViewModel(
            settingsRepository = get(),
            kitchen = get(),
            api = get()
        )
    }

    viewModel {
        RecentlyViewedViewModel(
            kitchen = get()
        )
    }
}
