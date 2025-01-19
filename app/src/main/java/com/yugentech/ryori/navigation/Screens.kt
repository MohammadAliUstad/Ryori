package com.yugentech.ryori.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("DEPRECATION")
sealed class Screens(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screens("feed", "Home", Icons.Default.Home)
    data object Explore : Screens("search", "Explore", Icons.Default.Search)
    data object Configure : Screens("configure", "Configure", Icons.Default.Build)
    data object AreaMealScreen : Screens("areaMeal/{area}", "Meal Area List", Icons.Default.List)
    data object CategoryMealScreen :
        Screens("categoryMeal/{category}", "Meal Category List", Icons.Default.List)

    data object MealScreen : Screens("meal/{meal}", "Meal", Icons.Default.List)
    data object About : Screens("about", "About", Icons.Default.Info)
}