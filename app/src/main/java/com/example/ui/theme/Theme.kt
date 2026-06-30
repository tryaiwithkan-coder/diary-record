package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onBackground = PureWhite,
    onSurface = PureWhite
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPrimary,
    secondary = SleekSoftPink,
    tertiary = SleekSoftYellow,
    background = SleekBackground,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = SleekDarkHeading,
    onTertiary = SleekDarkHeading,
    onBackground = SleekText,
    onSurface = SleekText,
    surfaceVariant = SleekSoftBlue,
    onSurfaceVariant = SleekDarkHeading,
    outline = SleekBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic material-you colors to preserve our customized warm pastel branding!
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
