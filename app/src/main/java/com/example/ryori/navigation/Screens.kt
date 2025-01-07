package com.example.ryori.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

@Suppress("DEPRECATION")
sealed class Screens(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screens("feed", "Home", Icons.Default.Home)
    object Explore : Screens("search", "Explore", Icons.Default.Search)
    object Configure : Screens("configure", "Configure", Icons.Default.Build)
    object AreaMealScreen : Screens("areaMeal/{area}", "Meal Area List", Icons.Default.List)
    object CategoryMealScreen : Screens("categoryMeal/{category}", "Meal Category List", Icons.Default.List)
    object MealScreen : Screens("meal/{meal}", "Meal", Icons.Default.List)
    object About : Screens("about", "About", Icons.Default.Info)
}