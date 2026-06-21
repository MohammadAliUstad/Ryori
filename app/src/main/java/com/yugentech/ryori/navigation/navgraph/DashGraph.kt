package com.yugentech.ryori.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.navigation.screen.AppScreen
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

    composable(AppScreen.Main.route) {
        MainScreen(
            onRecipeClick = openRecipe,
            onBrowse = openList,
            onAbout = {
                navController.navigate(AppScreen.About.route) { launchSingleTop = true }
            },
            onAppearance = {
                navController.navigate(AppScreen.Appearance.route) { launchSingleTop = true }
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
