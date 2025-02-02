package com.example.ryori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ryori.navigation.RyoriApp
import com.example.ryori.pref.DataStoreImpl
import com.example.ryori.ui.theme.RyoriTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val dataStore = DataStoreImpl(this)
        var initialTheme: String? = null

        runBlocking {
            initialTheme = dataStore.getTheme().first()
        }

        splashScreen.setKeepOnScreenCondition {
            initialTheme == null
        }

        setContent {
            var selectedTheme by remember { mutableStateOf(initialTheme) }
            val coroutineScope = rememberCoroutineScope()

            selectedTheme?.let { theme ->
                RyoriTheme(selectedTheme = theme) {
                    val backgroundColor = MaterialTheme.colorScheme.background
                    val isDarkTheme =
                        backgroundColor.luminance() < 0.5f

                    enableEdgeToEdge(
                        statusBarStyle = if (isDarkTheme) {
                            SystemBarStyle.dark(Color.Transparent.toArgb())
                        } else {
                            SystemBarStyle.light(
                                Color.Transparent.toArgb(),
                                MaterialTheme.colorScheme.primary.toArgb()
                            )
                        }
                    )

                    RyoriApp(
                        context = this,
                        onThemeChange = { newTheme ->
                            selectedTheme = newTheme
                            coroutineScope.launch {
                                dataStore.saveTheme(newTheme)
                            }
                        }
                    )
                }
            }
        }
    }
}