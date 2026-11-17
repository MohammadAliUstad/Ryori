package com.yugentech.ryori.navigation.host

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.yugentech.ryori.navigation.navgraph.configGraph
import com.yugentech.ryori.navigation.navgraph.dashGraph
import com.yugentech.ryori.navigation.screen.AppScreen
import com.yugentech.ryori.navigation.transition.rememberNavTransitions

@Composable
fun AppNavHost(
    navController: NavHostController,
    showOnboarding: Boolean = false,
    onOnboardingComplete: () -> Unit = {},
    shouldNavigateToHome: Boolean = false,
    onNavigatedToHome: () -> Unit = {}
) {
    // Sessions' Material 3 motion: shared axis X for opening and going back, fade through between
    // sections (onboarding and the app)
    val transitions = rememberNavTransitions()

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding) AppScreen.Onboarding.route else AppScreen.Main.route,
        enterTransition = { transitions.enter(this) },
        exitTransition = { transitions.exit(this) },
        popEnterTransition = { transitions.popEnter(this) },
        popExitTransition = { transitions.popExit(this) }
    ) {
        dashGraph(navController = navController, onOnboardingComplete = onOnboardingComplete)
        configGraph(navController = navController)
    }
}