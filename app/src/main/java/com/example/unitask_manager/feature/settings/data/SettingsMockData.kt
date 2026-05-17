package com.example.unitask_manager.feature.settings.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import com.example.unitask_manager.feature.settings.model.SettingsOption
import com.example.unitask_manager.feature.settings.model.SettingsOptionType
import com.example.unitask_manager.feature.settings.model.SettingsUserProfile

object SettingsMockData {

    val userProfile = SettingsUserProfile(
        displayName = "María García",
        email = "maria.garcia@universidad.edu",
        initials = "MG",
    )

    val accountOptions = listOf(
        SettingsOption(
            id = "edit_profile",
            title = "Editar perfil",
            subtitle = "Nombre, foto y datos personales",
            icon = Icons.Default.Edit,
        ),
        SettingsOption(
            id = "notifications",
            title = "Notificaciones",
            subtitle = "Recordatorios y alertas",
            icon = Icons.Default.Notifications,
        ),
    )

    val preferenceOptions = listOf(
        SettingsOption(
            id = "dark_theme",
            title = "Tema oscuro",
            subtitle = "Apariencia de la aplicación",
            icon = Icons.Default.DarkMode,
            type = SettingsOptionType.Toggle,
        ),
    )

    val supportOptions = listOf(
        SettingsOption(
            id = "privacy",
            title = "Privacidad",
            subtitle = "Datos y permisos",
            icon = Icons.Default.Lock,
        ),
        SettingsOption(
            id = "help",
            title = "Ayuda",
            subtitle = "Preguntas frecuentes y soporte",
            icon = Icons.AutoMirrored.Filled.Help,
        ),
        SettingsOption(
            id = "about",
            title = "Acerca de",
            subtitle = "Versión 1.0.0",
            icon = Icons.Default.Info,
        ),
    )

    val logoutOption = SettingsOption(
        id = "logout",
        title = "Cerrar sesión",
        icon = Icons.AutoMirrored.Filled.Logout,
        type = SettingsOptionType.Action,
    )
}
