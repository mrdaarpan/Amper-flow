package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentMagenta
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel

@Composable
fun DesignTab(
    viewModel: AmpereFlowViewModel,
    onNavigateToAodPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Collect AOD config flows reactively
    val aodActive by viewModel.preferences.alwaysOnDisplayActive.collectAsState()
    val clockStyle by viewModel.preferences.clockStyle.collectAsState()
    val speedometerStyle by viewModel.preferences.speedometerStyle.collectAsState()
    val speedometerColor by viewModel.preferences.speedometerColor.collectAsState()
    val speedometerBrightness by viewModel.preferences.speedometerBrightness.collectAsState()

    // Screen items
    val showBatteryDetails by viewModel.preferences.showBatteryDetails.collectAsState()
    val showWatts by viewModel.preferences.showWatts.collectAsState()
    val percentageAsPrimary by viewModel.preferences.percentageAsPrimary.collectAsState()
    val showMoreInfo by viewModel.preferences.showMoreInfo.collectAsState()
    val showMediaPlayer by viewModel.preferences.showMediaPlayer.collectAsState()
    val showNotifications by viewModel.preferences.showNotifications.collectAsState()
    val torchEnabled by viewModel.preferences.torchEnabled.collectAsState()
    val cameraEnabled by viewModel.preferences.cameraEnabled.collectAsState()

    // Behaviors
    val doubleTapToExit by viewModel.preferences.doubleTapToExit.collectAsState()
    val oledBurnInProtection by viewModel.preferences.oledBurnInProtection.collectAsState()
    val is24Hour by viewModel.preferences.is24Hour.collectAsState()

    val scrollState = rememberScrollState()

    val currentAccentColor = when (speedometerColor.lowercase()) {
        "green" -> AccentGreen
        "purple" -> AccentPurple
        "indigo" -> AccentIndigo
        "cyan" -> AccentCyan
        "magenta" -> AccentMagenta
        "yellow" -> AccentYellow
        else -> AccentGreen
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Core interactive device preview panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Phone frame mockup displaying the configured speedometer
                Box(
                    modifier = Modifier
                        .size(160.dp, 240.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.Black)
                        .border(3.dp, DarkBorder, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (is24Hour) "18:11" else "6:11",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Mini speedometer circle
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 4.dp,
                                    color = currentAccentColor.copy(alpha = speedometerBrightness),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "66.5%",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Widgets", color = TextPrimary)
                    }
                    Button(
                        onClick = { onNavigateToAodPreview() },
                        colors = ButtonDefaults.buttonColors(containerColor = currentAccentColor),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Preview", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activation toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = currentAccentColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Always On Display Activation",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Show when locked and charging",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
                Switch(
                    checked = aodActive,
                    onCheckedChange = { viewModel.preferences.setAlwaysOnDisplayActive(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = currentAccentColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "STYLE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = currentAccentColor,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Clock Style Option Row
        Text(
            text = "Clock Style",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val clockStyles = listOf("Off", "6:10", "Digital", "Analog")
            clockStyles.forEach { style ->
                val isSelected = clockStyle == style
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) currentAccentColor else DarkSurface)
                        .clickable { viewModel.preferences.setClockStyle(style) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speedometer Style Option Row
        Text(
            text = "Speedometer Style",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val speedometerStyles = listOf("Full Arc", "Broken Arc", "Dash Arc", "Tick Arc")
            speedometerStyles.forEach { style ->
                val isSelected = speedometerStyle == style
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) currentAccentColor else DarkSurface)
                        .clickable { viewModel.preferences.setSpeedometerStyle(style) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = style,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speedometer Color options circles
        Text(
            text = "Speedometer Color",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val colors = listOf("Magenta", "Purple", "Indigo", "Cyan", "Green", "Yellow")
            val colorDrawables = listOf(AccentMagenta, AccentPurple, AccentIndigo, AccentCyan, AccentGreen, AccentYellow)

            colors.forEachIndexed { index, colorName ->
                val isSelected = speedometerColor.lowercase() == colorName.lowercase()
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(colorDrawables[index])
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { viewModel.preferences.setSpeedometerColor(colorName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speedometer Brightness Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speedometer brightness",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Icon(
                imageVector = Icons.Default.BrightnessLow,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Slider(
            value = speedometerBrightness,
            onValueChange = { viewModel.preferences.setSpeedometerBrightness(it) },
            colors = SliderDefaults.colors(
                thumbColor = currentAccentColor,
                activeTrackColor = currentAccentColor,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "WHAT APPEARS ON SCREEN",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = currentAccentColor,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Configuration Toggles
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
        ) {
            ToggleRow(
                title = "Battery details on lock screen",
                subtitle = "Choose which readings show as the main value",
                checked = showBatteryDetails,
                onCheckedChange = { viewModel.preferences.setShowBatteryDetails(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Show Watts instead of mA",
                subtitle = "Show Current Wattage instead of milli-amperes",
                checked = showWatts,
                onCheckedChange = { viewModel.preferences.setShowWatts(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Set Percentage as Primary Display",
                subtitle = "Percentage is featured prominently on screen",
                checked = percentageAsPrimary,
                onCheckedChange = { viewModel.preferences.setPercentageAsPrimary(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Show More Info Button",
                subtitle = "Show info button to quickly view all battery info",
                checked = showMoreInfo,
                onCheckedChange = { viewModel.preferences.setShowMoreInfo(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Show Media Player Controls",
                subtitle = "Show controls for currently playing media content",
                checked = showMediaPlayer,
                onCheckedChange = { viewModel.preferences.setShowMediaPlayer(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Show Notifications on AOD",
                subtitle = "Notifications are subtly highlighted on display",
                checked = showNotifications,
                onCheckedChange = { viewModel.preferences.setShowNotifications(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Torch (tap & hold)",
                subtitle = "Enable or disable torch on lock screen",
                checked = torchEnabled,
                onCheckedChange = { viewModel.preferences.setTorchEnabled(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Camera (tap & hold)",
                subtitle = "Enable or disable camera on lock screen",
                checked = cameraEnabled,
                onCheckedChange = { viewModel.preferences.setCameraEnabled(it) },
                accentColor = currentAccentColor
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "BEHAVIOR",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = currentAccentColor,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
        ) {
            ToggleRow(
                title = "Double Tap to Exit",
                subtitle = "Double tap anywhere on black screen to close AOD preview",
                checked = doubleTapToExit,
                onCheckedChange = { viewModel.preferences.setDoubleTapToExit(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "Enable OLED Burn-In Protection",
                subtitle = "Shift screen contents dynamically to avoid screen retention",
                checked = oledBurnInProtection,
                onCheckedChange = { viewModel.preferences.setOledBurnInProtection(it) },
                accentColor = currentAccentColor
            )
            ToggleRow(
                title = "24-Hour Time Format",
                subtitle = "Format clock digits as 24-hour style",
                checked = is24Hour,
                onCheckedChange = { viewModel.preferences.setIs24Hour(it) },
                accentColor = currentAccentColor
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = accentColor
            )
        )
    }
}
