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
    primary = Color(0xFF0D0F13),            // Deep Black / Primary (keep)
    secondary = PurpleGrey80,               // Secondary (keep)
    tertiary = Blue,                        // Tertiary (keep)

    background = Color(0xFF121212),         // Umum untuk dark mode
    surface = Color(0xFF1A1C1E),            // Dark card/panel base

    onPrimary = Color(0xFFE0E0E0),          // Light gray text di atas primary
    onSecondary = Color(0xFFD1C4E9),        // Lighter versi PurpleGrey
    onBackground = Color(0xFFF5F5F5),       // White-ish text untuk background gelap
    onSurface = Color(0xFFF5F5F5),          // Sama, untuk text di surface

    error = Color(0xFFCF6679),              // Material error red (dark version)
    onError = Color(0xFF1A1C1E),            // Text hitam gelap di atas warna error

    outline = Color(0xFF2E2E2E),            // Border abu gelap
    surfaceVariant = Color(0xFF2C2C2C),     // Untuk kartu, field, atau area berbeda
    onSurfaceVariant = Color(0xFFB0BEC5)    // Abu terang untuk teks sekunder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),         // Indigo (soft)
    secondary = Color(0xFF9FA8DA),       // Soft Blue Gray
    tertiary = Color(0xFF4CAF50),        // Emerald Green (Accent / Success)
    background = Color(0xFFFAFAFA),      // Snow White
    surface = Color(0xFFFFFFFF),         // White
    onPrimary = Color(0xFFFFFFFF),       // White (teks di atas primary)
    onSecondary = Color(0xFF212121),     // Dark Charcoal (teks di atas secondary)
    onBackground = Color(0xFF212121),    // Dark Charcoal (teks di latar)
    onSurface = Color(0xFF212121),       // Dark Charcoal (teks di surface)
    error = Color(0xFFE57373),           // Soft Coral Red
    onError = Color(0xFF212121),         // Dark Charcoal (teks di error)
    outline = Color(0xFFE0E0E0),         // Light Gray (border/divider)
    surfaceVariant = Color(0xFFFAFAFA),   // Snow White (varian surface)
    onSurfaceVariant = Color(0xFF757575)  // Medium Gray (teks sekunder)
)

@Composable
fun SnapCashTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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