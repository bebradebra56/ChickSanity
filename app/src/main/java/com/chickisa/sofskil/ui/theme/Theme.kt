package com.chickisa.sofskil.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = LightGreen,
    onPrimaryContainer = Color(0xFF1B5E20),
    
    secondary = SecondaryYellow,
    onSecondary = Color.Black,
    secondaryContainer = LightYellow,
    onSecondaryContainer = Color(0xFF5C4A00),
    
    tertiary = AccentOrange,
    onTertiary = Color.White,
    tertiaryContainer = LightOrange,
    onTertiaryContainer = Color(0xFF5C3820),
    
    background = BackgroundBeige,  // #FFFDF5 - светлый кремовый
    onBackground = TextGray,
    
    surface = Color.White,  // Белые карточки
    onSurface = TextGray,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = TextGray,
    
    error = DangerRed,
    onError = Color.White,
    errorContainer = LightRed,
    onErrorContainer = Color(0xFF5C1C1A),
    
    outline = DividerColor,
    outlineVariant = Color(0xFFF0F0F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,  // #8BC34A - яркий зелёный
    onPrimary = Color(0xFF1A1A1A),  // Тёмный текст на зелёном
    primaryContainer = Color(0xFF2E5C1F),  // Тёмно-зелёный контейнер
    onPrimaryContainer = Color(0xFFC8E6C9),
    
    secondary = Color(0xFFFFCA28),  // Ярче жёлтый для тёмной темы
    onSecondary = Color(0xFF1A1A1A),
    secondaryContainer = Color(0xFF6B5A00),
    onSecondaryContainer = Color(0xFFFFF9C4),
    
    tertiary = Color(0xFFFF9F43),  // Ярче оранжевый
    onTertiary = Color(0xFF1A1A1A),
    tertiaryContainer = Color(0xFF6B3820),
    onTertiaryContainer = Color(0xFFFFE0B2),
    
    background = DarkBackground,  // #121212 - очень тёмный
    onBackground = Color(0xFFE8E8E8),  // Светлый текст
    
    surface = DarkSurface,  // #1E1E1E - тёмные карточки
    onSurface = Color(0xFFE8E8E8),  // Светлый текст
    surfaceVariant = DarkSurfaceVariant,  // #2A2A2A
    onSurfaceVariant = Color(0xFFC0C0C0),
    
    error = Color(0xFFEF5350),  // Ярче красный
    onError = Color.White,
    errorContainer = Color(0xFF5C1C1A),
    onErrorContainer = Color(0xFFFFCDD2),
    
    outline = DarkDivider,  // #3A3A3A
    outlineVariant = Color(0xFF2A2A2A)
)

@Composable
fun ChickSanityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
