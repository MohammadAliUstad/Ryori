@file:Suppress("DEPRECATION")

package com.example.ryori.navigation

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.ryori.pref.DataStoreImpl
import com.example.ryori.ui.screens.*
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

@OptIn(ExperimentalAnimationApi::class)
private fun defaultEnterTransition(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) +
            fadeIn(animationSpec = tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
private fun defaultExitTransition(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) +
            fadeOut(animationSpec = tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
private fun defaultPopEnterTransition(): EnterTransition {
    return slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) +
            fadeIn(animationSpec = tween(300))
}

@OptIn(ExperimentalAnimationApi::class)
private fun defaultPopExitTransition(): ExitTransition {
    return slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) +
            fadeOut(animationSpec = tween(300))
}