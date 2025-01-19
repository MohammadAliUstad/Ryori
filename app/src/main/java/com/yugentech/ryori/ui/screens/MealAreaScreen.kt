package com.yugentech.ryori.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yugentech.ryori.ui.components.MealCard
import com.yugentech.ryori.viewmodel.RyoriViewModel

@Composable
fun MealAreaScreen(
    navController: NavController,
    area: String,
    ryoriViewModel: RyoriViewModel = hiltViewModel()
) {
    val meals = ryoriViewModel.meals.collectAsState().value
    val isLoading = ryoriViewModel.isLoading.collectAsState().value
    val error = ryoriViewModel.error.collectAsState().value

    LaunchedEffect(area) {
        ryoriViewModel.fetchMealsByArea(area)
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (!error.isNullOrEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error: $error", color = Color.White)
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