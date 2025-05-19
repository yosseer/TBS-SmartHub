package com.example.tbssmarthub.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark color scheme for the application
 * Uses the TBS brand colors with dark mode adaptations
 */
private val DarkColorScheme = darkColorScheme(
    primary = TbsPrimaryGold,
    secondary = TbsSecondaryDark,
    tertiary = TbsTertiaryBlue,
    background = TbsBackgroundDark,
    surface = TbsSurfaceDark,
    onPrimary = TbsOnPrimaryDark,
    onSecondary = TbsOnSecondaryDark,
    onTertiary = TbsOnTertiaryDark,
    onBackground = TbsOnBackgroundDark,
    onSurface = TbsOnSurfaceDark
)

/**
 * Light color scheme for the application
 * Uses the TBS brand colors
 */
private val LightColorScheme = lightColorScheme(
    primary = TbsPrimaryGold,
    secondary = TbsSecondaryLight,
    tertiary = TbsTertiaryBlue,
    background = TbsBackgroundLight,
    surface = TbsSurfaceLight,
    onPrimary = TbsOnPrimaryLight,
    onSecondary = TbsOnSecondaryLight,
    onTertiary = TbsOnTertiaryLight,
    onBackground = TbsOnBackgroundLight,
    onSurface = TbsOnSurfaceLight
)

/**
 * Theme for the TBS SmartHub application
 * Supports both light and dark themes
 * 
 * @param darkTheme Whether to use dark theme
 * @param dynamicColor Whether to use dynamic color (Android 12+)
 * @param content Content to be themed
 */
@Composable
fun TbsSmartHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
