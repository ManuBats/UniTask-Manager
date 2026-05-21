package com.example.unitask_manager.feature.settings.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unitask_manager.feature.settings.data.SettingsMockData
import com.example.unitask_manager.feature.settings.ui.theme.SettingsColors

@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val option = SettingsMockData.logoutOption

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = SettingsColors.Logout,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = SettingsColors.Logout.copy(alpha = 0.4f),
        ),
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = option.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
