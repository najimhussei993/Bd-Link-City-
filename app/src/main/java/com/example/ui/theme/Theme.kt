package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGold,
    secondary = SecondaryRed,
    tertiary = TertiaryBlue,
    background = DarkBg,
    surface = SurfaceDark,
    onPrimary = CharcoalDark,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    outline = BorderColor
)

private val LightColorScheme = darkColorScheme( // Fallback is also beautiful dark for movie apps!
    primary = PrimaryGold,
    secondary = SecondaryRed,
    tertiary = TertiaryBlue,
    background = DarkBg,
    surface = SurfaceDark,
    onPrimary = CharcoalDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    outline = BorderColor
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for cinematic movie design
  dynamicColor: Boolean = false, // Use our solid curated cinematic dark theme!
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
