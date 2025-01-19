package com.yugentech.ryori.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yugentech.ryori.ui.components.AreaCard
import com.yugentech.ryori.ui.components.CategoryCard
import com.yugentech.ryori.ui.components.SearchBar
import com.yugentech.ryori.viewmodel.RyoriViewModel

@Composable
fun ExploreScreen(
    navController: NavController, viewModel: RyoriViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedSection by remember { mutableStateOf("Type") }
    val categories by viewModel.categories.collectAsState()
    val areas by viewModel.areas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
        viewModel.fetchAreas()
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
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBar(
                searchText = searchText,
                onSearchTextChange = { newText ->
                    searchText = newText
                },
                navController = navController
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { selectedSection = "Type" },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedSection == "Type") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (selectedSection == "Type") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("See by Type", style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = { selectedSection = "Region" },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedSection == "Region") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (selectedSection == "Region") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("See by Region", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedSection) {
                "Type" -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        items(categories.size) { index ->
                            val category = categories[index]
                            CategoryCard(category = category, onCategoryClick = {
                                navController.navigate("categoryMeal/${category.name}")
                            })
                        }
                    }
                }

                "Region" -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        items(areas.size) { index ->
                            val area = areas[index]
                            AreaCard(
                                area = area,
                                onAreaClick = {
                                    navController.navigate("areaMeal/${area.name}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}