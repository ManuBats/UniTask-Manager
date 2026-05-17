package com.example.unitask_manager.feature.settings.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class SettingsOptionType {
    Navigation,
    Toggle,
    Action,
}

data class SettingsOption(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val type: SettingsOptionType = SettingsOptionType.Navigation,
)

data class SettingsUserProfile(
    val displayName: String,
    val email: String,
    val initials: String,
)
