package com.yugentech.ryori.ui.config.settingsScreen

import android.text.format.Formatter
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.api.viewmodel.MoreViewModel
import com.yugentech.ryori.theme.service.HapticService
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.config.aboutScreen.components.ExitConfirmationDialog
import com.yugentech.ryori.ui.config.settingsScreen.components.ChefCard
import com.yugentech.ryori.ui.config.settingsScreen.components.ConfirmDialog
import com.yugentech.ryori.ui.config.settingsScreen.components.EditNameSheet
import com.yugentech.ryori.ui.config.settingsScreen.components.SettingsListItem
import com.yugentech.ryori.ui.config.settingsScreen.components.SettingsSwitchItem
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

// Which sheet or dialog is open. Saved by name so it survives rotation.
private enum class MoreOverlay { NONE, NAME, CLEAR_CACHE, CLEAR_RECENT, EXIT }

private data class AboutRow(val title: String, val subtitle: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
    onRecentlyViewed: () -> Unit,
    onWhatsNew: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: MoreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings = uiState.settings
    val context = LocalContext.current
    val haptic = koinInject<HapticService>()
    val view = LocalView.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var overlayName by rememberSaveable { mutableStateOf(MoreOverlay.NONE.name) }
    val overlay = MoreOverlay.valueOf(overlayName)
    val show: (MoreOverlay) -> Unit = { overlayName = it.name }
    val dismiss: () -> Unit = { overlayName = MoreOverlay.NONE.name }

    // Switch rows: a tick of feedback, then the change.
    val toggle: (Boolean, (Boolean) -> Unit) -> Unit = { value, set ->
        haptic.performTickHaptic(view)
        set(value)
    }

    val aboutRows = listOf(
        AboutRow("About Ryori", "Version info and the people behind it", onAbout),
        AboutRow("What's There", "Everything Ryori can do", onWhatsNew)
    )

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
                top = innerPadding.calculateTopPadding() + MaterialTheme.spacing.s,
                bottom = contentPadding.calculateBottomPadding() + MaterialTheme.spacing.s
            ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xxs)
        ) {
            item {
                ChefCard(
                    chefName = settings.chefName,
                    stats = uiState.stats,
                    onEditName = { show(MoreOverlay.NAME) }
                )
            }

            // --- Your kitchen ---
            item { SectionHeader(icon = Icons.Rounded.Kitchen, title = "Your Kitchen") }
            item {
                SettingsListItem(
                    title = "Recently viewed",
                    subtitle = when (uiState.recentCount) {
                        0 -> "Recipes you open will show up here"
                        1 -> "1 recipe"
                        else -> "${uiState.recentCount} recipes"
                    },
                    index = 0,
                    totalCount = 3,
                    onClick = onRecentlyViewed
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Vegetarian mode",
                    subtitle = "Hide meat and seafood dishes everywhere",
                    checked = settings.vegetarianMode,
                    index = 1,
                    totalCount = 3,
                    onCheckedChange = { toggle(it, viewModel::setVegetarianMode) },
                    onClick = { toggle(!settings.vegetarianMode, viewModel::setVegetarianMode) }
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Keep screen on",
                    subtitle = "No dimming while a recipe is open",
                    checked = settings.keepScreenOn,
                    index = 2,
                    totalCount = 3,
                    onCheckedChange = { toggle(it, viewModel::setKeepScreenOn) },
                    onClick = { toggle(!settings.keepScreenOn, viewModel::setKeepScreenOn) }
                )
            }

            // --- Appearance ---
            item { SectionHeader(icon = Icons.Rounded.Palette, title = "Appearance") }
            item {
                SettingsListItem(
                    title = "Theme & Colors",
                    subtitle = "Customize the app's look and feel",
                    index = 0,
                    totalCount = 2,
                    onClick = onAppearance
                )
            }
            item {
                SettingsSwitchItem(
                    title = "Haptic feedback",
                    subtitle = "Gentle vibrations on taps and toggles",
                    checked = settings.hapticsEnabled,
                    index = 1,
                    totalCount = 2,
                    onCheckedChange = { toggle(it, viewModel::setHaptics) },
                    onClick = { toggle(!settings.hapticsEnabled, viewModel::setHaptics) }
                )
            }

            // --- Storage ---
            item { SectionHeader(icon = Icons.Rounded.Storage, title = "Storage") }
            item {
                SettingsListItem(
                    title = "Clear cache",
                    subtitle = if (uiState.cacheSizeBytes > 0) {
                        "${Formatter.formatShortFileSize(context, uiState.cacheSizeBytes)} of recipes saved for offline use"
                    } else {
                        "Nothing cached yet"
                    },
                    index = 0,
                    totalCount = 2,
                    onClick = { show(MoreOverlay.CLEAR_CACHE) }
                )
            }
            item {
                SettingsListItem(
                    title = "Clear recently viewed",
                    subtitle = "Your kitchen stats are kept",
                    index = 1,
                    totalCount = 2,
                    onClick = { show(MoreOverlay.CLEAR_RECENT) }
                )
            }

            // --- About ---
            item { SectionHeader(icon = Icons.Rounded.Info, title = "About") }
            aboutRows.forEachIndexed { index, row ->
                item {
                    SettingsListItem(
                        title = row.title,
                        subtitle = row.subtitle,
                        index = index,
                        totalCount = aboutRows.size,
                        onClick = row.onClick
                    )
                }
            }

            // --- App ---
            item { SectionHeader(icon = Icons.AutoMirrored.Rounded.ExitToApp, title = "App") }
            item {
                SettingsListItem(
                    title = "Exit",
                    subtitle = "Close the Ryori app",
                    index = 0,
                    totalCount = 1,
                    onClick = { show(MoreOverlay.EXIT) }
                )
            }
        }
    }

    when (overlay) {
        MoreOverlay.NAME -> EditNameSheet(
            currentName = settings.chefName,
            onSave = {
                viewModel.setChefName(it)
                dismiss()
            },
            onDismiss = dismiss
        )

        MoreOverlay.CLEAR_CACHE -> ConfirmDialog(
            icon = Icons.Outlined.CleaningServices,
            title = "Clear cache?",
            message = "Saved recipes and lists will be downloaded again next time you open them. Your stats and settings stay.",
            confirmLabel = "Clear",
            onConfirm = viewModel::clearCache,
            destructive = true,
            onDismiss = dismiss
        )

        MoreOverlay.CLEAR_RECENT -> ConfirmDialog(
            icon = Icons.Outlined.DeleteSweep,
            title = "Clear recently viewed?",
            message = "This empties your recently viewed list. Your kitchen stats stay as they are.",
            confirmLabel = "Clear",
            onConfirm = viewModel::clearRecent,
            destructive = true,
            onDismiss = dismiss
        )

        MoreOverlay.EXIT -> ExitConfirmationDialog(
            onConfirm = { (context as? ComponentActivity)?.finish() },
            onDismiss = dismiss
        )

        MoreOverlay.NONE -> Unit
    }
}

