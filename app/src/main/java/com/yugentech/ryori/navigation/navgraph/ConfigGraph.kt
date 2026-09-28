package com.yugentech.ryori.navigation.navgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yugentech.ryori.navigation.screen.AppScreen
import com.yugentech.ryori.theme.viewmodel.ThemeViewModel
import com.yugentech.ryori.ui.config.aboutScreen.AboutScreen
import com.yugentech.ryori.ui.config.appearanceScreen.AppearanceScreen
import com.yugentech.ryori.ui.config.attributionsScreen.AttributionsScreen
import com.yugentech.ryori.ui.config.moreAppsScreen.MoreAppsScreen
import com.yugentech.ryori.ui.config.whatsNewScreen.WhatsNewScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.configGraph(
    navController: NavHostController
) {
    composable(AppScreen.Appearance.route) {
        val themeViewModel: ThemeViewModel = koinViewModel()
        AppearanceScreen(
            onNavigateBack = { navController.popBackStack() },
            themeViewModel = themeViewModel
        )
    }

    composable(AppScreen.About.route) {
        AboutScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToLicenses = {
                navController.navigate(AppScreen.Attributions.route) { launchSingleTop = true }
            },
            onNavigateToMoreApps = {
                navController.navigate(AppScreen.MoreApps.route) { launchSingleTop = true }
            }
        )
    }

    composable(AppScreen.WhatsNew.route) {
        WhatsNewScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(AppScreen.MoreApps.route) {
        MoreAppsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(AppScreen.Attributions.route) {
        AttributionsScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
