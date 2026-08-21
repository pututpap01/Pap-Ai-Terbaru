package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ImmersiveDarkColorScheme = darkColorScheme(
    primary = ImmersivePrimary,
    onPrimary = ImmersiveOnPrimary,
    primaryContainer = ImmersivePrimaryContainer,
    onPrimaryContainer = ImmersiveOnPrimaryContainer,
    secondary = ImmersiveSecondary,
    onSecondary = ImmersiveOnSecondary,
    secondaryContainer = ImmersiveSecondaryContainer,
    onSecondaryContainer = ImmersiveOnSecondaryContainer,
    tertiary = ImmersiveTertiary,
    onTertiary = ImmersiveOnTertiary,
    tertiaryContainer = ImmersiveTertiaryContainer,
    onTertiaryContainer = ImmersiveOnTertiaryContainer,
    background = ImmersiveBackground,
    onBackground = ImmersiveOnBackground,
    surface = ImmersiveSurface,
    onSurface = ImmersiveOnSurface,
    surfaceVariant = ImmersiveSurfaceVariant,
    onSurfaceVariant = ImmersiveOnSurfaceVariant,
    outline = ImmersiveOutline,
    outlineVariant = ImmersiveOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Immersive UI is an intentional dark-mode aesthetic with luminous purple accents
    MaterialTheme(
        colorScheme = ImmersiveDarkColorScheme,
        typography = Typography,
        content = content
    )
}
