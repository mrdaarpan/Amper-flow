package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BatteryState
import com.example.ui.components.CustomCircularSpeedometer
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel

@Composable
fun DetailsTab(
    viewModel: AmpereFlowViewModel,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val batteryState by viewModel.batteryState.collectAsState()
    val speedometerStyle by viewModel.preferences.speedometerStyle.collectAsState()
    val speedometerColorName by viewModel.preferences.speedometerColor.collectAsState()
    val speedometerBrightness by viewModel.preferences.speedometerBrightness.collectAsState()

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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Central Gauge Speedometer Component
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            CustomCircularSpeedometer(
                percentage = batteryState.percentageDecimal,
                statusText = if (batteryState.chargeStatus == "Charging") "FAST CHARGING" else batteryState.chargeStatus,
                subText = if (batteryState.chargeStatus == "Charging") "+${(batteryState.wattageW * 1000).toInt()} mW" else "",
                styleName = speedometerStyle,
                accentColor = accentColor,
                brightness = speedometerBrightness
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Monitoring Active Switch Box
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
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (batteryState.isMonitoringActive) AccentGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Monitoring active",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Powers charging stats, battery health, and AOD",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
                Button(
                    onClick = { viewModel.toggleMonitoring() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (batteryState.isMonitoringActive) DarkSurfaceVariant else AccentGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (batteryState.isMonitoringActive) "Turn off" else "Turn on",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (batteryState.isMonitoringActive) TextPrimary else Color.Black
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Grid of 8 stats parameters
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                StatGridCard(
                    title = "VOLTAGE",
                    value = "${batteryState.voltageMv} mV",
                    icon = Icons.Default.FlashOn,
                    iconColor = Color(0xFFFFEB3B),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voltage_card")
                        .clickable { onNavigateToDetail("Voltage") }
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatGridCard(
                    title = "CURRENT",
                    value = "${if (batteryState.currentMa > 0) "+" else ""}${batteryState.currentMa} mA",
                    icon = Icons.Default.Speed,
                    iconColor = AccentGreen,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("current_card")
                        .clickable { onNavigateToDetail("Current") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatGridCard(
                    title = "WATTAGE",
                    value = String.format("+%.1f W", batteryState.wattageW),
                    icon = Icons.Default.ElectricBolt,
                    iconColor = AccentGreen,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wattage_card")
                        .clickable { onNavigateToDetail("Wattage") }
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatGridCard(
                    title = "TEMPERATURE",
                    value = String.format("%.1f°C", batteryState.temperatureC),
                    icon = Icons.Default.DeviceThermostat,
                    iconColor = Color(0xFFFF9800),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("temperature_card")
                        .clickable { onNavigateToDetail("Temperature") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatGridCard(
                    title = "HEALTH",
                    value = "${batteryState.healthPercent}%",
                    icon = Icons.Default.Favorite,
                    iconColor = Color(0xFFE91E63),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDetail("Health") }
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatGridCard(
                    title = "PLUGGED",
                    value = batteryState.pluggedState,
                    icon = Icons.Default.Cable,
                    iconColor = Color(0xFF2196F3),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDetail("Plugged") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatGridCard(
                    title = "MAX CAPACITY",
                    value = "${batteryState.maxCapacityMah} mAh",
                    icon = Icons.Default.BatteryChargingFull,
                    iconColor = TextSecondary,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDetail("Max Capacity") }
                )
                Spacer(modifier = Modifier.width(12.dp))
                StatGridCard(
                    title = "CHARGE STATUS",
                    value = batteryState.chargeStatus,
                    icon = Icons.Default.Info,
                    iconColor = AccentGreen,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToDetail("Charge Status") }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Detailed scrolling cards (Benchmarking details)
        // Charge Time Card
        DetailRowCard(
            title = "Charge Time",
            mainText = "Full in 25m",
            subText = "At the current rate - typical times below",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToDetail("Charge Time") }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeColumn(label = "TO 80%", value = "2h 3m")
                TimeColumn(label = "TO 100%", value = "2h 42m")
                TimeColumn(label = "AVG SPEED", value = "5W")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Battery Health Card
        DetailRowCard(
            title = "Battery Health",
            mainText = "~95% Excellent",
            subText = "Measured at 65%",
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeColumn(label = "ESTIMATED", value = "3,970 mAh")
                TimeColumn(label = "REFERENCE", value = "4,181 mAh")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Charging Speed Card
        DetailRowCard(
            title = "Charging Speed",
            mainText = "Faster than 99% of device class",
            subText = "At 60-70% battery level",
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeColumn(label = "AVG SPEED", value = "5W")
                TimeColumn(label = "PEAK SPEED", value = "9W")
                TimeColumn(label = "YOUR DEVICE", value = "16W")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Charging Power by Level card
        DetailRowCard(
            title = "Charging Power by Level",
            mainText = "Average vs Peak W",
            subText = "AC Adaptor power limits by percentage",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                PowerLevelBar(range = "0-10%", avg = 3f, peak = 8f)
                PowerLevelBar(range = "10-20%", avg = 6f, peak = 25f)
                PowerLevelBar(range = "20-30%", avg = 5f, peak = 12f)
                PowerLevelBar(range = "30-40%", avg = 8f, peak = 23f)
                PowerLevelBar(range = "40-50%", avg = 6f, peak = 22f)
                PowerLevelBar(range = "50-60%", avg = 5f, peak = 11f)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Device Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DEVICE PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGreen
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                val deviceManufacturer = android.os.Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                val deviceModel = android.os.Build.MODEL
                val deviceBoard = android.os.Build.BOARD
                Text(
                    text = "$deviceManufacturer $deviceModel",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MANUFACTURER: ${deviceManufacturer.lowercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                    Text(
                        text = "BOARD: $deviceBoard",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun StatGridCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    fontSize = 20.sp
                )
            )
        }
    }
}

@Composable
fun DetailRowCard(
    title: String,
    mainText: String,
    subText: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGreen
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = mainText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
            content()
        }
    }
}

@Composable
fun TimeColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                fontSize = 10.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun PowerLevelBar(range: String, avg: Float, peak: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = range,
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
        ) {
            val width = size.width
            val height = size.height

            // Background empty track
            drawRect(
                color = DarkSurfaceVariant,
                size = size
            )

            // Draw Avg power bar
            val avgRatio = (avg / 30f).coerceIn(0f, 1f)
            drawRect(
                color = AccentGreen.copy(alpha = 0.5f),
                size = size.copy(width = width * avgRatio)
            )

            // Draw Peak power bar dot
            val peakRatio = (peak / 30f).coerceIn(0f, 1f)
            drawRect(
                color = AccentGreen,
                topLeft = Offset(width * avgRatio, 0f),
                size = size.copy(width = (width * peakRatio - width * avgRatio).coerceAtLeast(4f))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "${avg.toInt()}W / ${peak.toInt()}W",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}
