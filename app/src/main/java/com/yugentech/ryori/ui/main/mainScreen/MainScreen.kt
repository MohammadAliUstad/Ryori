package com.yugentech.ryori.ui.main.mainScreen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.yugentech.ryori.api.model.domain.RecipeFilter
import com.yugentech.ryori.api.model.domain.RecipeType
import com.yugentech.ryori.navigation.screen.BottomBarScreen
import com.yugentech.ryori.ui.config.aboutScreen.components.ExitConfirmationDialog
import com.yugentech.ryori.ui.config.settingsScreen.MoreScreen
import com.yugentech.ryori.ui.main.exploreScreen.ExploreScreen
import com.yugentech.ryori.ui.main.homeScreen.HomeScreen
import com.yugentech.ryori.ui.main.mainScreen.components.BottomNavBar

private val tabs = BottomBarScreen.all

// Same structure as Quill's MainScreen: the Scaffold only owns the bottom bar, each tab owns
// its own Scaffold + top bar, tabs switch with a directional slide, and each tab's state
// (scroll position etc.) is kept while you're on another tab.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onRecipeClick: (RecipeType, String) -> Unit,
    onBrowse: (RecipeFilter, String) -> Unit,
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
    onRecentlyViewed: () -> Unit,
    onWhatsNew: () -> Unit,
    requestedTab: String? = null,
    onTabRequestHandled: () -> Unit = {}
) {
    val context = LocalContext.current

    val homeIndex = tabs.indexOf(BottomBarScreen.Home)
    // Saved as an index since the BottomBarScreen objects themselves aren't saveable.
    var currentTabIndex by rememberSaveable { mutableIntStateOf(homeIndex) }
    val currentTab = tabs[currentTabIndex]

    // e.g. "Find something to cook" on an empty Recently viewed screen lands on Search.
    LaunchedEffect(requestedTab) {
        if (requestedTab == null) return@LaunchedEffect
        tabs.indexOfFirst { it.route == requestedTab }
            .takeIf { it >= 0 }
            ?.let { currentTabIndex = it }
        onTabRequestHandled()
    }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        if (currentTabIndex != homeIndex) {
            currentTabIndex = homeIndex
        } else {
            showExitDialog = true
        }
    }

    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = { (context as? ComponentActivity)?.finish() },
            onDismiss = { showExitDialog = false }
        )
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { screen -> currentTabIndex = tabs.indexOf(screen) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    val navigatingRight = tabs.indexOf(targetState) > tabs.indexOf(initialState)

                    if (navigatingRight) {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    } else {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    }
                },
                label = "TabSwitch",
                modifier = Modifier.fillMaxSize()
            ) { tab ->
                saveableStateHolder.SaveableStateProvider(tab.route) {
                    when (tab) {
                        BottomBarScreen.Home -> HomeScreen(
                            onRecipeClick = onRecipeClick,
                            onBrowse = onBrowse,
                            contentPadding = innerPadding
                        )

                        BottomBarScreen.Search -> ExploreScreen(
                            onRecipeClick = onRecipeClick,
                            onBrowse = onBrowse,
                            contentPadding = innerPadding
                        )

                        BottomBarScreen.More -> MoreScreen(
                            onAbout = onAbout,
                            onAppearance = onAppearance,
                            onRecentlyViewed = onRecentlyViewed,
                            onWhatsNew = onWhatsNew,
                            contentPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}
