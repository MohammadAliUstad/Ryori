package com.yugentech.ryori

import android.content.Context
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.yugentech.ryori.api.viewmodel.OnboardingViewModel
import com.yugentech.ryori.localization.AppLocale
import com.yugentech.ryori.navigation.host.AppNavHost
import com.yugentech.ryori.theme.RyoriTheme
import com.yugentech.ryori.theme.ThemeRevealHost
import com.yugentech.ryori.theme.config.ThemeMode
import com.yugentech.ryori.theme.viewmodel.ThemeViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    // Below Android 13, the language picked in the app (see AppLocale); from 13 the system's
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(AppLocale.wrap(newBase))
    }

    private val themeViewModel: ThemeViewModel by viewModel()
    private val onboardingViewModel: OnboardingViewModel by viewModel()

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
            !animationReady.value || !themeViewModel.isLoaded.value ||
                onboardingViewModel.showOnboarding.value == null
        }

        setContent {
            val navController = rememberNavController()
            val themeConfiguration by themeViewModel.themeConfiguration.collectAsStateWithLifecycle()
            val hapticsEnabled by themeViewModel.hapticsEnabled.collectAsStateWithLifecycle()
            val showOnboarding by onboardingViewModel.showOnboarding.collectAsStateWithLifecycle()

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

            RyoriTheme(themeConfiguration = themeConfiguration, hapticsEnabled = hapticsEnabled) {
                // Outermost, so a light/dark wipe covers the whole screen.
                ThemeRevealHost {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Waits for the saved flag (the splash covers that), so the start screen is
                        // picked once and never swaps under the user.
                        val firstRun = showOnboarding
                        if (firstRun != null) {
                            // Fixed at first composition: finishing the intro navigates away itself.
                            val startOnOnboarding = remember { firstRun }
                            AppNavHost(
                                navController = navController,
                                showOnboarding = startOnOnboarding,
                                onOnboardingComplete = onboardingViewModel::complete
                            )
                        }
                    }
                }
            }
        }
    }
}