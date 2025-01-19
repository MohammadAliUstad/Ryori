package com.yugentech.ryori.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yugentech.ryori.data.model.domain.DomainArea

@Composable
fun AreaCard(
    area: DomainArea,
    onAreaClick: (String) -> Unit
) {
    if (area.name == "Unknown" || area.name == "Vietnamese" || area.name == "Ukrainian") {
        return
    } else {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onAreaClick(area.name ?: "") }
                .width(120.dp),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = area.name ?: "Unknown Area",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}