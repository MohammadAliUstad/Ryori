package com.yugentech.ryori.navigation.navgraph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.navigation.screen.AppScreen
import com.yugentech.ryori.navigation.screen.BottomBarScreen
import com.yugentech.ryori.ui.config.recentlyViewedScreen.RecentlyViewedScreen
import com.yugentech.ryori.ui.main.mainScreen.MainScreen
import com.yugentech.ryori.ui.main.recipeListScreen.RecipeListScreen
import com.yugentech.ryori.ui.main.recipeScreen.RecipeScreen

fun NavGraphBuilder.dashGraph(
    navController: NavHostController
) {
    val openRecipe: (RecipeType, String) -> Unit = { type, id ->
        navController.navigate(AppScreen.Recipe.createRoute(type, id))
    }
    val openList: (RecipeFilter, String) -> Unit = { filter, value ->
        navController.navigate(AppScreen.RecipeList.createRoute(filter, value))
    }

    val open: (AppScreen) -> Unit = { screen ->
        navController.navigate(screen.route) { launchSingleTop = true }
    }

    composable(AppScreen.Main.route) { backStackEntry ->
        // Screens pushed on top of Main can ask for a different tab when they pop back to it.
        val requestedTab by backStackEntry.savedStateHandle
            .getStateFlow<String?>(AppScreen.REQUESTED_TAB, null)
            .collectAsState()

        MainScreen(
            onRecipeClick = openRecipe,
            onBrowse = openList,
            onAbout = { open(AppScreen.About) },
            onAppearance = { open(AppScreen.Appearance) },
            onRecentlyViewed = { open(AppScreen.RecentlyViewed) },
            onWhatsNew = { open(AppScreen.WhatsNew) },
            requestedTab = requestedTab,
            onTabRequestHandled = { backStackEntry.savedStateHandle.set<String?>(AppScreen.REQUESTED_TAB, null) }
        )
    }

    composable(AppScreen.RecentlyViewed.route) {
        RecentlyViewedScreen(
            onBack = { navController.popBackStack() },
            onRecipeClick = openRecipe,
            onExplore = {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set<String?>(AppScreen.REQUESTED_TAB, BottomBarScreen.Search.route)
                navController.popBackStack()
            }
        )
    }

    // Arguments are read raw: Navigation decodes the URL-encoded path segments itself.
    composable(AppScreen.RecipeList.route) { backStackEntry ->
        val filter = backStackEntry.arguments?.getString("filter")
            ?.let { runCatching { RecipeFilter.valueOf(it) }.getOrNull() }
            ?: RecipeFilter.CATEGORY
        val value = backStackEntry.arguments?.getString("value").orEmpty()

        RecipeListScreen(
            filter = filter,
            value = value,
            onBack = { navController.popBackStack() },
            onRecipeClick = openRecipe
        )
    }

    composable(AppScreen.Recipe.route) { backStackEntry ->
        val type = backStackEntry.arguments?.getString("type")
            ?.let { runCatching { RecipeType.valueOf(it) }.getOrNull() }
            ?: RecipeType.MEAL
        val id = backStackEntry.arguments?.getString("id").orEmpty()

        RecipeScreen(
            type = type,
            id = id,
            onBack = { navController.popBackStack() },
            onRecipeClick = openRecipe,
            onBrowse = openList
        )
    }
}
