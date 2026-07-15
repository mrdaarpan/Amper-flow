package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel

@Composable
fun SettingsTab(
    viewModel: AmpereFlowViewModel,
    modifier: Modifier = Modifier
) {
    val theme by viewModel.preferences.theme.collectAsState()
    val appLanguage by viewModel.preferences.appLanguage.collectAsState()
    val tempUnit by viewModel.preferences.temperatureUnit.collectAsState()
    val dualCellMode by viewModel.preferences.dualCellBatteryMode.collectAsState()

    // Alarm Limits
    val chargingLimit by viewModel.preferences.batteryChargingLimit.collectAsState()
    val chargingLimitEnabled by viewModel.preferences.batteryChargingLimitEnabled.collectAsState()
    
    val dischargingLimit by viewModel.preferences.batteryDischargingLimit.collectAsState()
    val dischargingLimitEnabled by viewModel.preferences.batteryDischargingLimitEnabled.collectAsState()

    val tempLimit by viewModel.preferences.batteryTemperatureLimit.collectAsState()
    val tempLimitEnabled by viewModel.preferences.batteryTemperatureLimitEnabled.collectAsState()

    val alarmSound by viewModel.preferences.alarmSound.collectAsState()
    val notificationFormat by viewModel.preferences.notificationFormat.collectAsState()

    val speedometerColorName by viewModel.preferences.speedometerColor.collectAsState()
    val accentColor = when (speedometerColorName.lowercase()) {
        "green" -> AccentGreen
        "purple" -> Color(0xFFBB86FC)
        "indigo" -> Color(0xFF3F51B5)
        "cyan" -> Color(0xFF00E5FF)
        "magenta" -> Color(0xFFFF4081)
        "yellow" -> Color(0xFFFFEB3B)
        else -> AccentGreen
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "SETTINGS & ALERTS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // General Category
        Text(
            text = "General",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
        ) {
            SettingsItem(
                title = "Theme",
                subtitle = theme,
                icon = Icons.Default.Palette,
                iconColor = accentColor,
                onClick = {
                    val nextTheme = when (theme) {
                        "Follow system" -> "Light"
                        "Light" -> "Dark"
                        else -> "Follow system"
                    }
                    viewModel.preferences.setTheme(nextTheme)
                }
            )
            SettingsItem(
                title = "App Language",
                subtitle = appLanguage,
                icon = Icons.Default.Language,
                iconColor = accentColor,
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Units Category
        Text(
            text = "Units",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
        ) {
            SettingsItem(
                title = "Temperature Unit",
                subtitle = tempUnit,
                icon = Icons.Default.Thermostat,
                iconColor = accentColor,
                onClick = {
                    val nextUnit = if (tempUnit == "Celsius") "Fahrenheit" else "Celsius"
                    viewModel.preferences.setTemperatureUnit(nextUnit)
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dual Cell Battery Mode",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "Correct current readings on multi-cell architectures",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
                Checkbox(
                    checked = dualCellMode,
                    onCheckedChange = { viewModel.preferences.setDualCellBatteryMode(it) },
                    colors = CheckboxDefaults.colors(checkedColor = accentColor, uncheckedColor = TextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Alerts Category
        Text(
            text = "Alerts & Limits",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .padding(16.dp)
        ) {
            // Charging Limit Alarm
            AlertConfigRow(
                title = "Battery Charging Limit",
                subtitle = if (chargingLimitEnabled) "Alarm trigger level: $chargingLimit%" else "No limit set.",
                checked = chargingLimitEnabled,
                onCheckedChange = { viewModel.preferences.setBatteryChargingLimitEnabled(it) },
                accentColor = accentColor
            )
            if (chargingLimitEnabled) {
                Slider(
                    value = chargingLimit.toFloat(),
                    onValueChange = { viewModel.preferences.setBatteryChargingLimit(it.toInt()) },
                    valueRange = 50f..100f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Discharging Limit Alarm
            AlertConfigRow(
                title = "Battery Discharge Limit",
                subtitle = if (dischargingLimitEnabled) "Alarm trigger level: $dischargingLimit%" else "No limit set.",
                checked = dischargingLimitEnabled,
                onCheckedChange = { viewModel.preferences.setBatteryDischargingLimitEnabled(it) },
                accentColor = accentColor
            )
            if (dischargingLimitEnabled) {
                Slider(
                    value = dischargingLimit.toFloat(),
                    onValueChange = { viewModel.preferences.setBatteryDischargingLimit(it.toInt()) },
                    valueRange = 5f..40f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature Limit Alarm
            AlertConfigRow(
                title = "Battery Temperature Limit",
                subtitle = if (tempLimitEnabled) "Alarm trigger temperature: $tempLimit°C" else "No limit set.",
                checked = tempLimitEnabled,
                onCheckedChange = { viewModel.preferences.setBatteryTemperatureLimitEnabled(it) },
                accentColor = accentColor
            )
            if (tempLimitEnabled) {
                Slider(
                    value = tempLimit.toFloat(),
                    onValueChange = { viewModel.preferences.setBatteryTemperatureLimit(it.toInt()) },
                    valueRange = 30f..55f,
                    colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // System & Permissions Category
        Text(
            text = "System & Permissions",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
        ) {
            SettingsItem(
                title = "Disable Battery Optimization",
                subtitle = "Prevent OS from killing background monitoring threads",
                icon = Icons.Default.Security,
                iconColor = accentColor,
                onClick = {}
            )
            SettingsItem(
                title = "Clear Charge History",
                subtitle = "Permanently clear the database logs",
                icon = Icons.Default.Security,
                iconColor = Color.Red,
                onClick = { viewModel.clearAllHistory() }
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }
    }
}

@Composable
fun AlertConfigRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = accentColor)
        )
    }
}
