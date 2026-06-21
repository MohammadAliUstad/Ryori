package com.yugentech.ryori.ui.config.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.config.settingsScreen.components.SettingsListItem
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "More",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = MaterialTheme.spacing.m,
                end = MaterialTheme.spacing.m,
                top = innerPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.s
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            item {
                SectionHeader(
                    icon = Icons.Default.Palette,
                    title = "Appearance"
                )
            }
            item {
                SettingsListItem(
                    title = "Theme & Colors",
                    subtitle = "Customize the app's look and feel",
                    index = 0,
                    totalCount = 1,
                    onClick = onAppearance
                )
            }

            item {
                SectionHeader(
                    icon = Icons.Default.Info,
                    title = "About"
                )
            }
            item {
                SettingsListItem(
                    title = "About Ryori",
                    subtitle = "Version info and credits",
                    index = 0,
                    totalCount = 1,
                    onClick = onAbout
                )
            }
        }
    }
}
