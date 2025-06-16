package com.example.snapcash.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2C6BE9),         // Indigo Light (agar tetap tegas tapi tidak menusuk)
    secondary = Color(0xFF9FA8DA),       // Soft Blue Gray (tetap tenang di dark mode)
    tertiary = Color(0xFF80C683),        // Emerald Green lebih terang (success)
    background = Color(0xFF121212),      // Deep Gray/Black
    surface = Color(0xFF1E1E1E),         // Abu gelap (untuk card/panel)
    onPrimary = Color(0xFFF5F5F5),       // Hitam (untuk teks di atas primary terang)
    onSecondary = Color(0xFFE0E0E0),     // Light Gray (teks di atas secondary)
    onBackground = Color(0xFFE0E0E0),    // Light Gray (teks utama di background)
    onSurface = Color(0xFFF5F5F5),       // Hampir putih (untuk teks di panel gelap)
    error = Color.Red,           // Soft Red terang (untuk dark background)
    onError = Color(0xFF000000),         // Hitam (teks di atas error)
    outline = Color(0xFF444444),         // Abu tua untuk garis/border
    surfaceVariant = Color(0xFF2C2C2C),  // Varian surface lebih gelap (untuk diferensiasi UI)
    onSurfaceVariant = Color(0xFFB0B0B0) // Medium-light gray (teks sekunder)
)



private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2C6BE9),         // Indigo (soft)
    secondary = Color(0xFF9FA8DA),       // Soft Blue Gray
    tertiary = Color(0xFF4CAF50),        // Emerald Green (Accent / Success)
    background = Color(0xFFFAFAFA),      // Snow White
    surface = Color(0xFFFFFFFF),         // White
    onPrimary = Color(0xFFFFFFFF),       // White (teks di atas primary)
    onSecondary = Color(0xFF212121),     // Dark Charcoal (teks di atas secondary)
    onBackground = Color(0xFF212121),    // Dark Charcoal (teks di latar)
    onSurface = Color(0xFF212121),       // Dark Charcoal (teks di surface)
    error = Color.Red,           // Soft Coral Red
    onError = Color(0xFF212121),         // Dark Charcoal (teks di error)
    outline = Color(0xFFE0E0E0),         // Light Gray (border/divider)
    surfaceVariant = Color(0xFFFAFAFA),   // Snow White (varian surface)
    onSurfaceVariant = Color(0xFF757575),  // Medium Gray (teks sekunder)
)

@Composable
fun SnapCashTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}