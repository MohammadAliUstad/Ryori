package com.yugentech.ryori.ui.main.mainScreen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.yugentech.ryori.theme.tokens.icons
import com.yugentech.ryori.theme.tokens.spacing

// compact = true uses Quill's tighter settings-page spacing (16dp top / 8dp bottom) for stacked
// settings sections such as Appearance; the default (24dp / 12dp) separates content sections.
@Composable
fun SectionHeader(
    icon: ImageVector,
    title: String,
    compact: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = MaterialTheme.spacing.m,
                bottom = if (compact) MaterialTheme.spacing.s else MaterialTheme.spacing.sm,
                top = if (compact) MaterialTheme.spacing.m else MaterialTheme.spacing.l
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(MaterialTheme.icons.mediumSmall)
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}