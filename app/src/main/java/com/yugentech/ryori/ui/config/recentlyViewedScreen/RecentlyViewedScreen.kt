package com.yugentech.ryori.ui.config.recentlyViewedScreen

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.R
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.api.viewmodel.RecentlyViewedViewModel
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.config.settingsScreen.components.ConfirmDialog
import com.yugentech.ryori.ui.main.mainScreen.components.RecipeCard
import org.koin.androidx.compose.koinViewModel

// The last recipes the user opened, newest first (stored in Room, see KitchenRepository).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyViewedScreen(
    onBack: () -> Unit,
    onRecipeClick: (RecipeType, String) -> Unit,
    onExplore: () -> Unit,
    viewModel: RecentlyViewedViewModel = koinViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    var confirmClear by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Recently viewed")
                        val count = recipes?.size ?: 0
                        if (count > 0) {
                            Text(
                                text = if (count == 1) "1 recipe" else "$count recipes",
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
                actions = {
                    if (!recipes.isNullOrEmpty()) {
                        IconButton(onClick = { confirmClear = true }) {
                            Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear recently viewed")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        val list = recipes
        when {
            // Still reading from the database: a blank frame rather than a flash of the empty state.
            list == null -> Box(Modifier.fillMaxSize())

            list.isEmpty() -> EmptyRecent(
                onExplore = onExplore,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = MaterialTheme.spacing.xl)
            )

            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(
                    start = MaterialTheme.spacing.m,
                    end = MaterialTheme.spacing.m,
                    top = innerPadding.calculateTopPadding() + MaterialTheme.spacing.s,
                    bottom = navBarPadding.calculateBottomPadding()
                ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.s),
                modifier = Modifier.fillMaxSize()
            ) {
                items(list, key = { "${it.type}_${it.id}" }) { recipe ->
                    RecipeCard(recipe = recipe, onClick = { onRecipeClick(it.type, it.id) })
                }
            }
        }
    }

    if (confirmClear) {
        ConfirmDialog(
            icon = Icons.Outlined.DeleteSweep,
            title = "Clear recently viewed?",
            message = "This empties your recently viewed list. Your kitchen stats stay as they are.",
            confirmLabel = "Clear",
            onConfirm = viewModel::clear,
            destructive = true,
            onDismiss = { confirmClear = false }
        )
    }
}

@Composable
private fun EmptyRecent(
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.whoa),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(200.dp)
        )
        Spacer(Modifier.height(MaterialTheme.spacing.l))
        Text(
            text = "Nothing cooked up yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(MaterialTheme.spacing.xs))
        Text(
            text = "Recipes you open will appear here, so you can find your way back to them.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(MaterialTheme.spacing.l))
        Button(onClick = onExplore) { Text("Find something to cook") }
    }
}
