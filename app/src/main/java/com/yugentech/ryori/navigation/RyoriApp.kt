@file:Suppress("DEPRECATION")

package com.yugentech.ryori.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.yugentech.ryori.ui.components.ExitConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RyoriApp(
    onThemeChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val scrollState = rememberScrollState()
    val currentDestination =
        navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

    val showBackArrow =
        currentDestination != Screens.Home.route && currentDestination != Screens.Explore.route

    val topAppBarColor = if (scrollState.value > 0) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    BackHandler {
        showExitDialog = true
    }
    if (showExitDialog) {
        ExitConfirmationDialog(
            onConfirm = { (context as? ComponentActivity)?.finish() },
            onDismiss = { showExitDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ryori",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                },
                navigationIcon = {
                    AnimatedContent(
                        targetState = showBackArrow,
                        transitionSpec = {
                            if (targetState) {
                                slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() with slideOutHorizontally { it } + fadeOut()
                            }
                        }, label = ""
                    ) { targetBackArrow ->
                        if (targetBackArrow) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .size(45.dp)
                                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.LunchDining,
                                contentDescription = "App Icon",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .size(45.dp)
                                    .fillMaxSize()
                                    .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topAppBarColor
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            AppNavHost(
                context = context,
                navController = navController,
                scrollState = scrollState,
                onThemeChange = onThemeChange
            )
        }
    }
}