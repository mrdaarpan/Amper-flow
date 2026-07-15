package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    secondary = TextSecondary,
    tertiary = AccentCyan,
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),
    secondary = Color(0xFF757575),
    tertiary = Color(0xFF00ACC1),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEEEEEE),
    outline = Color(0xFFE0E0E0)
)

@Composable
fun AmpereFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColorName: String = "Green",
    content: @Composable () -> Unit
) {
    // Determine dynamic primary accent color based on user preference
    val primaryAccent = when (accentColorName.lowercase()) {
        "green" -> AccentGreen
        "purple" -> AccentPurple
        "indigo" -> AccentIndigo
        "cyan" -> AccentCyan
        "magenta" -> AccentMagenta
        "yellow" -> AccentYellow
        else -> AccentGreen
    }

    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(
            primary = primaryAccent,
            tertiary = if (accentColorName.lowercase() == "cyan") AccentGreen else AccentCyan
        )
    } else {
        LightColorScheme.copy(
            primary = primaryAccent
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
