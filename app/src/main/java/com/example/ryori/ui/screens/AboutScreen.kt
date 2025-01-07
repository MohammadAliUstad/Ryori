package com.example.ryori.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    val context = LocalContext.current // Use context here for Toast

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // App Logo or Icon
        Icon(
            imageVector = Icons.Default.RestaurantMenu, // Replace with your app's logo/icon
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(top = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        // App Name
        Text(
            text = "Ryori",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp)
        )

        // App Description
        Text(
            text = "Ryori is your ultimate food app for exploring delicious meals and discovering amazing recipes worldwide. Built with love for food enthusiasts.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Developer Information
        Text(
            text = "Developed by Thara Bhai Joginder",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )

        // App Version
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Contact Buttons
        ContactButton(
            icon = Icons.Default.Email,
            label = "Contact Developer",
            onClick = {
                Toast.makeText(context, "Jo Joginder", Toast.LENGTH_SHORT).show()
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ContactButton(
            icon = Icons.Default.OpenInBrowser,
            label = "Visit GitHub",
            onClick = {
                Toast.makeText(context, "Thara bhai Joginder", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun ContactButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit // Fixed type
) {
    OutlinedButton(
        onClick = onClick, // Correctly pass the lambda
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary
        )
    }
}