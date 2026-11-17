package com.yugentech.ryori.ui.config.settingsScreen

import androidx.compose.ui.res.pluralStringResource
import com.yugentech.ryori.ui.main.mainScreen.components.ScrimTopAppBar
import com.yugentech.ryori.localization.AppLocale
import android.text.format.Formatter
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.R
import com.yugentech.ryori.api.viewmodel.MoreViewModel
import com.yugentech.ryori.theme.tokens.spacing
import com.yugentech.ryori.ui.config.aboutScreen.components.ExitConfirmationDialog
import com.yugentech.ryori.ui.config.moreAppsScreen.MoreFromUsSheet
import com.yugentech.ryori.ui.config.settingsScreen.components.ChefCard
import com.yugentech.ryori.ui.config.settingsScreen.components.ConfirmDialog
import com.yugentech.ryori.ui.config.settingsScreen.components.EditNameSheet
import com.yugentech.ryori.ui.config.settingsScreen.components.SettingsListItem
import com.yugentech.ryori.ui.config.settingsScreen.components.SettingsSwitchItem
import com.yugentech.ryori.ui.main.mainScreen.components.SectionHeader
import com.yugentech.ryori.ui.main.mainScreen.components.ToastMessage
import org.koin.androidx.compose.koinViewModel

// Which sheet or dialog is open. Saved by name so it survives rotation.
private enum class MoreOverlay { NONE, NAME, CLEAR_CACHE, CLEAR_RECENT, EXIT, MORE_APPS }

private data class AboutRow(val title: String, val subtitle: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onAbout: () -> Unit,
    onAppearance: () -> Unit,
    onLanguage: () -> Unit = {},
    onRecentlyViewed: () -> Unit,
    onWhatsNew: () -> Unit,
    onMoreApps: () -> Unit,
    onReplayOnboarding: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: MoreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val notice by viewModel.notice.collectAsState()
    val settings = uiState.settings
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var overlayName by rememberSaveable { mutableStateOf(MoreOverlay.NONE.name) }
    val overlay = MoreOverlay.valueOf(overlayName)
    val show: (MoreOverlay) -> Unit = { overlayName = it.name }
    val dismiss: () -> Unit = { overlayName = MoreOverlay.NONE.name }

    // Switch rows: a toggle-on or toggle-off pulse, then the change.
    val toggle: (Boolean, (Boolean) -> Unit) -> Unit = { value, set ->
        haptic.performHapticFeedback(if (value) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff)
        set(value)
    }

    val aboutRows = listOf(
        AboutRow(stringResource(R.string.settings_about_ryori), stringResource(R.string.version), onAbout),
        AboutRow(stringResource(R.string.settings_whats_there), stringResource(R.string.settings_whats_there_subtitle), onWhatsNew),
        AboutRow(stringResource(R.string.settings_replay_intro), stringResource(R.string.settings_replay_intro_subtitle), onReplayOnboarding),
        AboutRow(stringResource(R.string.settings_more_from_us), stringResource(R.string.settings_more_from_us_subtitle)) { show(MoreOverlay.MORE_APPS) }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // Transparent like Home's: a scrim fades in once content scrolls under it
            ScrimTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tab_settings),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                item { SectionHeader(icon = Icons.Rounded.Kitchen, title = stringResource(R.string.settings_section_kitchen)) }
                item {
                    SettingsListItem(
                        title = stringResource(R.string.settings_recently_viewed),
                        subtitle = when (uiState.recentCount) {
                            0 -> stringResource(R.string.settings_recently_viewed_empty)
                            else -> pluralStringResource(R.plurals.settings_recent_count, uiState.recentCount, uiState.recentCount)
                        },
                        index = 0,
                        totalCount = 3,
                        onClick = onRecentlyViewed
                    )
                }
                item {
                    SettingsSwitchItem(
                        title = stringResource(R.string.settings_vegetarian),
                        subtitle = stringResource(R.string.settings_vegetarian_subtitle),
                        checked = settings.vegetarianMode,
                        checkedIcon = Icons.Rounded.Eco,
                        index = 1,
                        totalCount = 3,
                        onCheckedChange = { toggle(it, viewModel::setVegetarianMode) },
                        onClick = { toggle(!settings.vegetarianMode, viewModel::setVegetarianMode) }
                    )
                }
                item {
                    SettingsSwitchItem(
                        title = stringResource(R.string.settings_keep_screen_on),
                        subtitle = stringResource(R.string.settings_keep_screen_on_subtitle),
                        checked = settings.keepScreenOn,
                        checkedIcon = Icons.Rounded.LightMode,
                        index = 2,
                        totalCount = 3,
                        onCheckedChange = { toggle(it, viewModel::setKeepScreenOn) },
                        onClick = { toggle(!settings.keepScreenOn, viewModel::setKeepScreenOn) }
                    )
                }
    
                // --- Appearance ---
                item { SectionHeader(icon = Icons.Rounded.Palette, title = stringResource(R.string.settings_section_appearance)) }
                item {
                    SettingsListItem(
                        title = stringResource(R.string.settings_theme_colors),
                        subtitle = stringResource(R.string.settings_theme_colors_subtitle),
                        index = 0,
                        totalCount = 3,
                        onClick = onAppearance
                    )
                }
                item {
                    SettingsListItem(
                        title = stringResource(R.string.language_title),
                        subtitle = AppLocale.current(context)?.nativeName ?: stringResource(R.string.language_system_default),
                        index = 1,
                        totalCount = 3,
                        onClick = onLanguage
                    )
                }
                item {
                    SettingsSwitchItem(
                        title = stringResource(R.string.settings_haptics),
                        subtitle = stringResource(R.string.settings_haptics_subtitle),
                        checked = settings.hapticsEnabled,
                        checkedIcon = Icons.Rounded.Vibration,
                        index = 2,
                        totalCount = 3,
                        onCheckedChange = { toggle(it, viewModel::setHaptics) },
                        onClick = { toggle(!settings.hapticsEnabled, viewModel::setHaptics) }
                    )
                }
    
                // --- Storage ---
                item { SectionHeader(icon = Icons.Rounded.Storage, title = stringResource(R.string.settings_section_storage)) }
                item {
                    SettingsListItem(
                        title = stringResource(R.string.settings_clear_cache),
                        subtitle = if (uiState.cacheSizeBytes > 0) {
                            stringResource(R.string.settings_clear_cache_subtitle, Formatter.formatShortFileSize(context, uiState.cacheSizeBytes))
                        } else {
                            stringResource(R.string.settings_clear_cache_empty)
                        },
                        index = 0,
                        totalCount = 2,
                        onClick = { show(MoreOverlay.CLEAR_CACHE) }
                    )
                }
                item {
                    SettingsListItem(
                        title = stringResource(R.string.settings_clear_recent),
                        subtitle = stringResource(R.string.settings_clear_recent_subtitle),
                        index = 1,
                        totalCount = 2,
                        onClick = { show(MoreOverlay.CLEAR_RECENT) }
                    )
                }
    
                // --- About ---
                item { SectionHeader(icon = Icons.Rounded.Info, title = stringResource(R.string.about)) }
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
                item { SectionHeader(icon = Icons.AutoMirrored.Rounded.ExitToApp, title = stringResource(R.string.settings_section_app)) }
                item {
                    SettingsListItem(
                        title = stringResource(R.string.settings_exit),
                        subtitle = stringResource(R.string.settings_exit_subtitle),
                        index = 0,
                        totalCount = 1,
                        onClick = { show(MoreOverlay.EXIT) }
                    )
                }
            }

            // A setting that failed to save (e.g. storage full).
            ToastMessage(
                message = notice?.let { stringResource(it) },
                onDismiss = viewModel::dismissNotice,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = innerPadding.calculateTopPadding() + MaterialTheme.spacing.s)
            )
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
            title = stringResource(R.string.settings_clear_cache_title),
            message = stringResource(R.string.settings_clear_cache_message),
            confirmLabel = stringResource(R.string.clear),
            onConfirm = viewModel::clearCache,
            destructive = true,
            onDismiss = dismiss
        )

        MoreOverlay.CLEAR_RECENT -> ConfirmDialog(
            title = stringResource(R.string.settings_clear_recent_title),
            message = stringResource(R.string.settings_clear_recent_message),
            confirmLabel = stringResource(R.string.clear),
            onConfirm = viewModel::clearRecent,
            destructive = true,
            onDismiss = dismiss
        )

        MoreOverlay.EXIT -> ExitConfirmationDialog(
            onConfirm = { (context as? ComponentActivity)?.finish() },
            onDismiss = dismiss
        )

        MoreOverlay.MORE_APPS -> MoreFromUsSheet(
            onDismiss = dismiss,
            onSeeAll = onMoreApps
        )

        MoreOverlay.NONE -> Unit
    }
}

