package com.example.unitask_manager.feature.settings.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object SettingsColors {
    val PrimaryPurple = Color(0xFF7C3AED)
    val PrimaryDark = Color(0xFF5B21B6)
    val PrimaryLight = Color(0xFFEDE9FE)
    val Background = Color(0xFFF5F3FF)
    val CardSurface = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF1F2937)
    val TextSecondary = Color(0xFF6B7280)
    val TextOnPrimary = Color(0xFFFFFFFF)
    val Divider = Color(0xFFE5E7EB)
    val Logout = Color(0xFFDC2626)
    val LogoutContainer = Color(0xFFFEE2E2)
    val IconTint = Color(0xFF7C3AED)
    val IconContainer = Color(0xFFEDE9FE)

    val LightColorScheme = lightColorScheme(
        primary = PrimaryPurple,
        onPrimary = TextOnPrimary,
        primaryContainer = PrimaryLight,
        onPrimaryContainer = PrimaryDark,
        background = Background,
        onBackground = TextPrimary,
        surface = CardSurface,
        onSurface = TextPrimary,
        surfaceVariant = PrimaryLight,
        onSurfaceVariant = TextSecondary,
        outline = Divider,
        error = Logout,
        onError = TextOnPrimary,
        errorContainer = LogoutContainer,
        onErrorContainer = Logout,
    )

    val DarkColorScheme = darkColorScheme(
        primary = Color(0xFFA78BFA),
        onPrimary = Color(0xFF1F2937),
        primaryContainer = PrimaryDark,
        onPrimaryContainer = Color(0xFFEDE9FE),
        background = Color(0xFF111827),
        onBackground = Color(0xFFF9FAFB),
        surface = Color(0xFF1F2937),
        onSurface = Color(0xFFF9FAFB),
        surfaceVariant = Color(0xFF374151),
        onSurfaceVariant = Color(0xFF9CA3AF),
        outline = Color(0xFF4B5563),
        error = Color(0xFFF87171),
        onError = Color(0xFF1F2937),
        errorContainer = Color(0xFF7F1D1D),
        onErrorContainer = Color(0xFFFECACA),
    )
}
