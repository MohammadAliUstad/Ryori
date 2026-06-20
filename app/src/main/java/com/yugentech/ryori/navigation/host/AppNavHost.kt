package com.yugentech.ryori.navigation.host

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.yugentech.ryori.navigation.navgraph.configGraph
import com.yugentech.ryori.navigation.navgraph.dashGraph
import com.yugentech.ryori.navigation.screen.AppScreen
import com.yugentech.ryori.navigation.transition.defaultEnterTransition
import com.yugentech.ryori.navigation.transition.defaultExitTransition
import com.yugentech.ryori.navigation.transition.defaultPopEnterTransition
import com.yugentech.ryori.navigation.transition.defaultPopExitTransition

@Composable
fun AppNavHost(
    navController: NavHostController,
    shouldNavigateToHome: Boolean = false,
    onNavigatedToHome: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Main.route,
        enterTransition = { defaultEnterTransition() },
        exitTransition = { defaultExitTransition() },
        popEnterTransition = { defaultPopEnterTransition() },
        popExitTransition = { defaultPopExitTransition() }
    ) {
        dashGraph(navController = navController)
        configGraph(navController = navController)
    }
}