package com.yugentech.ryori.navigation.screen

import android.net.Uri
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeType

sealed class AppScreen(val route: String) {

    data object Main        : AppScreen("main")

    data object Home        : AppScreen("home")
    data object Search      : AppScreen("search")
    data object More        : AppScreen("more")

    data object About       : AppScreen("about")
    data object Appearance  : AppScreen("appearance")
    data object Configure   : AppScreen("configure")
    data object WhatsNew    : AppScreen("whats_new")
    data object Attributions : AppScreen("attributions")
    data object MoreApps    : AppScreen("more_apps")
    data object RecentlyViewed : AppScreen("recently_viewed")

    companion object {
        // Set on Main's back stack entry to switch tabs when returning to it.
        const val REQUESTED_TAB = "requested_tab"
    }

    // A meal or drink, looked up by id.
    data object Recipe : AppScreen("recipe/{type}/{id}") {
        fun createRoute(type: RecipeType, id: String) = "recipe/${type.name}/${Uri.encode(id)}"
    }

    // A grid of recipes filtered by category / cuisine / ingredient / letter / drink category.
    // The value is URL-encoded: drink categories like "Coffee / Tea" contain slashes.
    data object RecipeList : AppScreen("recipes/{filter}/{value}") {
        fun createRoute(filter: RecipeFilter, value: String) =
            "recipes/${filter.name}/${Uri.encode(value)}"
    }
}
