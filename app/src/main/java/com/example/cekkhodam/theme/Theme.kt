package com.example.cekkhodam.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val CosmicColorScheme = darkColorScheme(
    primary = CosmicGold,
    secondary = PurpleSpark,
    tertiary = MysticOrchid,
    background = CosmicBgStart,
    surface = GlassSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimary = Color.Black
)

@Composable
fun CekKhodamTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
      colorScheme = CosmicColorScheme,
      typography = Typography,
      content = content
  )
}
