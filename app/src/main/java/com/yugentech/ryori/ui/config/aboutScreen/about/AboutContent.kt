package com.yugentech.ryori.ui.config.aboutScreen.about

import com.yugentech.ryori.R
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.core.net.toUri
import com.yugentech.ryori.utils.AppConstants

// The rows of the About screen, grouped the same way as Quill's.
object AboutContent {

    fun getSupportItems(
        context: Context,
        onMoreAppsClick: () -> Unit
    ): List<AboutOption> {
        return listOf(
            AboutOption(
                title = R.string.contact_developer,
                subtitle = R.string.about_contact_subtitle,
                icon = Icons.Default.Email,
                onClick = { context.open(Intent(Intent.ACTION_SENDTO, AppConstants.SUPPORT_EMAIL.toUri())) }
            ),
            AboutOption(
                title = R.string.about_coffee,
                subtitle = R.string.about_coffee_subtitle,
                icon = Icons.Default.LocalCafe,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.KOFI_URL.toUri())) }
            ),
            AboutOption(
                title = R.string.visit_github,
                subtitle = R.string.view_source_code_and_contribute,
                icon = Icons.Default.Code,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.GITHUB_URL.toUri())) }
            ),
            AboutOption(
                title = R.string.settings_more_from_us,
                subtitle = R.string.about_more_apps_subtitle,
                icon = Icons.Default.Apps,
                onClick = onMoreAppsClick
            )
        )
    }

    fun getCommunityItems(context: Context): List<AboutOption> {
        return listOf(
            AboutOption(
                title = R.string.about_rate,
                subtitle = R.string.about_rate_subtitle,
                icon = Icons.Default.StarRate,
                onClick = {
                    // Play Store app if installed, otherwise the store's web page.
                    val opened = context.open(Intent(Intent.ACTION_VIEW, AppConstants.MARKET_URL.toUri()))
                    if (!opened) context.open(Intent(Intent.ACTION_VIEW, AppConstants.PLAY_STORE_URL.toUri()))
                }
            ),
            AboutOption(
                title = R.string.about_share,
                subtitle = R.string.about_share_subtitle,
                icon = Icons.Default.Share,
                onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.about_share_message, AppConstants.PLAY_STORE_URL))
                    }
                    context.open(Intent.createChooser(send, context.getString(R.string.about_share_chooser)))
                }
            )
        )
    }

    fun getLegalItems(
        context: Context,
        onNavigateToLicenses: () -> Unit
    ): List<AboutOption> {
        return listOf(
            AboutOption(
                title = R.string.about_privacy,
                subtitle = null,
                icon = Icons.Default.Policy,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.PRIVACY_POLICY_URL.toUri())) }
            ),
            AboutOption(
                title = R.string.about_terms,
                subtitle = null,
                icon = Icons.Default.Gavel,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.TERMS_OF_SERVICE_URL.toUri())) }
            ),
            AboutOption(
                title = R.string.attributions,
                subtitle = R.string.about_attributions_subtitle,
                icon = Icons.Default.Description,
                onClick = onNavigateToLicenses
            )
        )
    }

    // Starting an activity throws if nothing can handle the intent (e.g. no email app).
    private fun Context.open(intent: Intent): Boolean =
        runCatching { startActivity(intent) }.isSuccess
}
