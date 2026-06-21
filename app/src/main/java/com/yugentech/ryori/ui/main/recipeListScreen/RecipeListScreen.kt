package com.yugentech.ryori.ui.main.recipeListScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.model.domain.titleFor
import com.yugentech.ryori.api.viewmodel.RecipeListViewModel
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCard
import org.koin.androidx.compose.koinViewModel

// One grid screen for every "browse" destination: a category, cuisine, ingredient, letter,
// drink category, or all non-alcoholic drinks.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    filter: RecipeFilter,
    value: String,
    onBack: () -> Unit,
    onRecipeClick: (RecipeType, String) -> Unit,
    viewModel: RecipeListViewModel = koinViewModel()
) {
    LaunchedEffect(filter, value) { viewModel.load(filter, value) }

    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = filter.titleFor(value), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!uiState.isLoading && uiState.error == null) {
                            Text(
                                text = "${uiState.recipes.size} recipes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.error != null || uiState.recipes.isEmpty() -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(MaterialTheme.spacing.xl)
            ) {
                Text(
                    text = if (uiState.error != null) "Couldn't load recipes" else "Nothing here yet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(MaterialTheme.spacing.xs))
                Text(
                    text = uiState.error ?: "No recipes match this yet. Try another one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                if (uiState.error != null) {
                    Spacer(Modifier.height(MaterialTheme.spacing.m))
                    Button(onClick = { viewModel.load(filter, value, force = true) }) { Text("Try again") }
                }
            }

            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(
                    start = MaterialTheme.spacing.m,
                    end = MaterialTheme.spacing.m,
                    top = innerPadding.calculateTopPadding() + MaterialTheme.spacing.s,
                    bottom = navBarPadding.calculateBottomPadding() + MaterialTheme.spacing.s
                ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.recipes, key = { "${it.type}_${it.id}" }) { recipe ->
                    RecipeCard(recipe = recipe, onClick = { onRecipeClick(it.type, it.id) })
                }
            }
        }
    }
}
