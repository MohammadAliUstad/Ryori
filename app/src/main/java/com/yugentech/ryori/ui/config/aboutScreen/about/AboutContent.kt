package com.yugentech.ryori.ui.config.aboutScreen.about

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
                title = "Contact Developer",
                subtitle = "Get in touch for support",
                icon = Icons.Default.Email,
                onClick = { context.open(Intent(Intent.ACTION_SENDTO, AppConstants.SUPPORT_EMAIL.toUri())) }
            ),
            AboutOption(
                title = "Buy me a coffee",
                subtitle = "Support the development",
                icon = Icons.Default.LocalCafe,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.KOFI_URL.toUri())) }
            ),
            AboutOption(
                title = "Visit GitHub",
                subtitle = "View source code and contribute",
                icon = Icons.Default.Code,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.GITHUB_URL.toUri())) }
            ),
            AboutOption(
                title = "More from us",
                subtitle = "Discover other apps we've built",
                icon = Icons.Default.Apps,
                onClick = onMoreAppsClick
            )
        )
    }

    fun getCommunityItems(context: Context): List<AboutOption> {
        return listOf(
            AboutOption(
                title = "Rate Ryori",
                subtitle = "Leave a review on the Play Store",
                icon = Icons.Default.StarRate,
                onClick = {
                    // Play Store app if installed, otherwise the store's web page.
                    val opened = context.open(Intent(Intent.ACTION_VIEW, AppConstants.MARKET_URL.toUri()))
                    if (!opened) context.open(Intent(Intent.ACTION_VIEW, AppConstants.PLAY_STORE_URL.toUri()))
                }
            ),
            AboutOption(
                title = "Share with Friends",
                subtitle = "Send it to a fellow foodie",
                icon = Icons.Default.Share,
                onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, AppConstants.SHARE_MESSAGE)
                    }
                    context.open(Intent.createChooser(send, "Share Ryori"))
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
                title = "Privacy Policy",
                subtitle = null,
                icon = Icons.Default.Policy,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.PRIVACY_POLICY_URL.toUri())) }
            ),
            AboutOption(
                title = "Terms of Service",
                subtitle = null,
                icon = Icons.Default.Gavel,
                onClick = { context.open(Intent(Intent.ACTION_VIEW, AppConstants.TERMS_OF_SERVICE_URL.toUri())) }
            ),
            AboutOption(
                title = "Attributions",
                subtitle = "Open source & credits",
                icon = Icons.Default.Description,
                onClick = onNavigateToLicenses
            )
        )
    }

    // Starting an activity throws if nothing can handle the intent (e.g. no email app).
    private fun Context.open(intent: Intent): Boolean =
        runCatching { startActivity(intent) }.isSuccess
}
