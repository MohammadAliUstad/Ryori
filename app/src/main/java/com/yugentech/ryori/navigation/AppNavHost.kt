@file:Suppress("DEPRECATION")

package com.yugentech.ryori.navigation

import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.yugentech.ryori.pref.DataStoreImpl
import com.yugentech.ryori.ui.screens.AboutScreen
import com.yugentech.ryori.ui.screens.ConfigureScreen
import com.yugentech.ryori.ui.screens.ExploreScreen
import com.yugentech.ryori.ui.screens.HomeScreen
import com.yugentech.ryori.ui.screens.MealAreaScreen
import com.yugentech.ryori.ui.screens.MealCategoryScreen
import com.yugentech.ryori.ui.screens.MealScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(
    context: Context,
    navController: NavHostController = rememberAnimatedNavController(),
    scrollState: ScrollState,
    onThemeChange: (String) -> Unit
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        composable(Screens.Home.route) {
            HomeScreen(scrollState = scrollState)
        }
        composable(Screens.Explore.route) {
            ExploreScreen(navController)
        }
        composable(Screens.Configure.route) {
            ConfigureScreen(
                navController = navController,
                onThemeChange = onThemeChange,
                dataStore = DataStoreImpl(context)
            )
        }
        composable(Screens.AreaMealScreen.route) { backStackEntry ->
            val area = backStackEntry.arguments?.getString("area") ?: ""
            MealAreaScreen(area = area, navController = navController)
        }
        composable(Screens.CategoryMealScreen.route) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            MealCategoryScreen(category = category, navController = navController)
        }
        composable(Screens.MealScreen.route) { backStackEntry ->
            val meal = backStackEntry.arguments?.getString("meal") ?: ""
            MealScreen(mealName = meal)
        }
        composable(Screens.About.route) {
            AboutScreen()
        }
    }
}

private fun defaultEnterTransition(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) +
            fadeIn(animationSpec = tween(300))
}

private fun defaultExitTransition(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) +
            fadeOut(animationSpec = tween(300))
}

private fun defaultPopEnterTransition(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) +
            fadeIn(animationSpec = tween(300))
}

private fun defaultPopExitTransition(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) +
            fadeOut(animationSpec = tween(300))
}