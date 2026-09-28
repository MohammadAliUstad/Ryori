package com.yugentech.ryori

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.yugentech.ryori.navigation.host.AppNavHost
import com.yugentech.ryori.theme.RyoriTheme
import com.yugentech.ryori.theme.config.ThemeMode
import com.yugentech.ryori.theme.viewmodel.ThemeViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Same order as Quill: the splash screen is installed before super.onCreate.
        val splashScreen = installSplashScreen()

        // Like Quill, the splash stays up for a minimum time so its animation can play. On top
        // of that it waits for the saved theme, so the first frame already uses the right
        // light/dark mode instead of flashing the placeholder light theme.
        val animationReady = MutableStateFlow(false)
        lifecycleScope.launch {
            delay(1000.milliseconds)
            animationReady.value = true
        }

        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            !animationReady.value || !themeViewModel.isLoaded.value
        }

        setContent {
            val navController = rememberNavController()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()

            val darkTheme = when (themeConfiguration.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(scrim = Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
                },
                navigationBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(scrim = Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
                }
            )

            RyoriTheme(themeConfiguration = themeConfiguration) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(
                        navController = navController
                    )
                }
            }
        }
    }
}