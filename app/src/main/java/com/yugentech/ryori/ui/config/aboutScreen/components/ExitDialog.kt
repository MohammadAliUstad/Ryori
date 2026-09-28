package com.yugentech.ryori.ui.config.aboutScreen.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.runtime.Composable
import com.yugentech.ryori.ui.main.mainScreen.components.RyoriDialog

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    RyoriDialog(
        icon = Icons.AutoMirrored.Outlined.ExitToApp,
        title = "Exit App",
        message = "Are you sure you want to exit Ryori?",
        confirmLabel = "Exit",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
