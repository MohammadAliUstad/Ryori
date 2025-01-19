package com.yugentech.ryori.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yugentech.ryori.ui.components.MealCard
import com.yugentech.ryori.viewmodel.RyoriViewModel

@Composable
fun MealCategoryScreen(
    navController: NavController,
    category: String,
    viewModel: RyoriViewModel = hiltViewModel(),
) {
    val meals by viewModel.meals.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(category) {
        viewModel.fetchMealsByCategory(category)
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(meals.size) { index ->
                val meal = meals[index]
                MealCard(
                    meal = meal,
                    onMealClick = {
                        navController.navigate("meal/${meal.name}")
                    }
                )
            }
        }
    }
}