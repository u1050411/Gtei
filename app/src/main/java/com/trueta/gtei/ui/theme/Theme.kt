package com.trueta.gtei.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * GteiTheme is a Composable function responsible for applying the theme to the Composables
 * within the passed 'content' lambda.
 *
 * @param darkTheme Flag to indicate whether the dark theme should be applied.
 * @param dynamicColor Flag to indicate whether dynamic color should be applied (Android 12+).
 * @param content A lambda that contains the Composables to which the theme should be applied.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GteiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine the color scheme based on the system settings and API level
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Get the current Composable's view to adjust system UI
    val view = LocalView.current

    // If the Composable is not in edit mode, apply system UI adjustments
    if (!view.isInEditMode) {
        SideEffect {
            // Change the status bar color and light status bars flag
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    // Apply the Material Theme to the Composables within 'content'
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
