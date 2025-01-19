package com.yugentech.ryori

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yugentech.ryori.navigation.RyoriApp
import com.yugentech.ryori.pref.DataStoreImpl
import com.yugentech.ryori.ui.theme.RyoriTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        val dataStore = DataStoreImpl(this)
        var initialTheme: String?

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
                    val isDarkTheme = backgroundColor.luminance() < 0.5f

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