@file:Suppress("DEPRECATION")

package com.example.ryori.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ryori.navigation.Screens
import com.example.ryori.pref.DataStore
import com.example.ryori.ui.components.AboutComponent
import com.example.ryori.ui.components.ThemeOption
import kotlinx.coroutines.flow.first

@Composable
fun ConfigureScreen(
    navController: NavController,
    onThemeChange: (String) -> Unit,
    dataStore: DataStore
) {
    var selectedTheme by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        selectedTheme = dataStore.getTheme().first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Select Theme",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Light", "Dark", "System", "Black", "White").forEach { theme ->
                ThemeOption(
                    theme = theme,
                    isSelected = theme == selectedTheme,
                    onClick = {
                        selectedTheme = theme
                        onThemeChange(theme)
                    }
                )
            }
        }

        AboutComponent(
            title = "About",
            subtitle = "App version and information",
            icon = {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "About Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            onClick = { navController.navigate(Screens.About.route) }
        )
    }
}