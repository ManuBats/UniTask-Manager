package com.example.unitask_manager.feature.settings.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unitask_manager.feature.settings.model.SettingsOption
import com.example.unitask_manager.feature.settings.model.SettingsOptionType
import com.example.unitask_manager.feature.settings.ui.theme.SettingsColors

@Composable
fun SettingsSectionCard(
    title: String,
    options: List<SettingsOption>,
    modifier: Modifier = Modifier,
    darkThemeEnabled: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = SettingsColors.PrimaryPurple,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            options.forEachIndexed { index, option ->
                val isLast = index == options.lastIndex
                SettingsOptionRow(
                    option = option,
                    showDivider = !isLast,
                    toggleChecked = darkThemeEnabled,
                    onToggleChange = onDarkThemeChange,
                    onClick = { },
                )
            }
        }
    }
}
