package com.maiki.rok_helper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RoKColors {
    val bg = Color(0xFF0D1017)
    val surface = Color(0xFF161B22)
    val card = Color(0xFF1C2129)
    val border = Color(0xFF30363D)
    val text1 = Color(0xFFE6EDF3)
    val text2 = Color(0xFF8B949E)
    val gold = Color(0xFFFFD700)
    val green = Color(0xFF3FB950)
    val red = Color(0xFFF85149)
    val blue = Color(0xFF58A6FF)
    
    val food = Color(0xFF8BC34A)
    val wood = Color(0xFF8D6E63)
    val stone = Color(0xFF90A4AE)
    val time = Color(0xFF42A5F5)
    val power = Color(0xFFFFD54F)
}

private val DarkColorScheme = darkColorScheme(
    primary = RoKColors.gold,
    secondary = RoKColors.blue,
    tertiary = RoKColors.green,
    background = RoKColors.bg,
    surface = RoKColors.surface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = RoKColors.text1,
    onSurface = RoKColors.text1
)

@Composable
fun RoKHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // В Telegram Web App обычно используется темная тема или тема из системы
    val colorScheme = DarkColorScheme // Пока оставим только темную для стиля RoK

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
