package com.yugentech.ryori.navigation.screen

import com.yugentech.ryori.R
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    @param:StringRes val title: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomBarScreen(
        route = AppScreen.Home.route,
        title = R.string.tab_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    // The Explore tab (still "Search" in code and routes; shown as "Explore").
    data object Search : BottomBarScreen(
        route = AppScreen.Search.route,
        title = R.string.tab_explore,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )

    // The settings tab (still "More" in code and routes; shown as "Settings").
    data object More : BottomBarScreen(
        route = AppScreen.More.route,
        title = R.string.tab_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val all = listOf(Home, Search, More)

        fun fromRoute(route: String): BottomBarScreen =
            all.firstOrNull { it.route == route } ?: Home
    }
}