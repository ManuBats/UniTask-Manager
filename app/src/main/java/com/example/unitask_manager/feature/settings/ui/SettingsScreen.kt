package com.example.unitask_manager.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unitask_manager.feature.settings.data.SettingsMockData
import com.example.unitask_manager.feature.settings.ui.components.LogoutButton
import com.example.unitask_manager.feature.settings.ui.components.ProfileHeaderSection
import com.example.unitask_manager.feature.settings.ui.components.SettingsSectionCard
import com.example.unitask_manager.feature.settings.ui.theme.SettingsColors
import com.example.unitask_manager.feature.settings.ui.theme.SettingsTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    var darkThemeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SettingsColors.Background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ProfileHeaderSection(profile = SettingsMockData.userProfile)
            }

            item {
                SettingsSectionCard(
                    title = "Cuenta",
                    options = SettingsMockData.accountOptions,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                SettingsSectionCard(
                    title = "Preferencias",
                    options = SettingsMockData.preferenceOptions,
                    darkThemeEnabled = darkThemeEnabled,
                    onDarkThemeChange = { darkThemeEnabled = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                SettingsSectionCard(
                    title = "Soporte",
                    options = SettingsMockData.supportOptions,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                LogoutButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = { },
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    SettingsTheme {
        SettingsScreen()
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Tema oscuro (preview)")
@Composable
private fun SettingsScreenDarkPreview() {
    SettingsTheme(darkTheme = true) {
        SettingsScreen()
    }
}
