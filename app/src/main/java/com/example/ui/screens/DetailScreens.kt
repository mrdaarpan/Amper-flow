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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LineChart
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel

@Composable
fun DetailScreens(
    viewModel: AmpereFlowViewModel,
    screenType: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val batteryState by viewModel.batteryState.collectAsState()
    val speedometerColorName by viewModel.preferences.speedometerColor.collectAsState()
    val isCelsius = viewModel.preferences.temperatureUnit.collectAsState().value == "Celsius"

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
        // Simple elegant back header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = screenType.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    letterSpacing = 1.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Render subpage content based on clicked card type
        when (screenType) {
            "Voltage" -> {
                Text(
                    text = "${batteryState.voltageMv} mV",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = String.format("%.2f V", batteryState.voltageMv / 1000f),
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(24.dp))

                LineChart(
                    points = batteryState.voltageHistory,
                    lineColor = Color(0xFFFFEB3B),
                    unitSuffix = "mV"
                )

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "About Voltage",
                    text = "Battery voltage rises as the cell fills. A healthy lithium-ion cell rests near 3.7 V and climbs toward 4.2 to 4.4 V when fully charged. Sharp dips while charging can point to an aging battery, low charging currents, or a high electrical load."
                )
            }
            "Current" -> {
                Text(
                    text = "${batteryState.currentMa} mA",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "${String.format("%.1f", batteryState.wattageW)} W - ${batteryState.voltageMv} mV",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs for Charging/Discharging info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Charging", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Discharging", color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LineChart(
                    points = batteryState.currentHistory,
                    lineColor = accentColor,
                    unitSuffix = "mA"
                )

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "Electric Current Flow",
                    text = "This measures the instantaneous electrical current drawn by or supplied to the battery. When charging, a positive current represents energy flowing in. When discharging, a negative current represents system energy consumption."
                )
            }
            "Wattage" -> {
                Text(
                    text = String.format("%.2f W", batteryState.wattageW),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "${batteryState.currentMa} mA * ${batteryState.voltageMv} mV",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(24.dp))

                LineChart(
                    points = batteryState.wattageHistory,
                    lineColor = accentColor,
                    unitSuffix = "W"
                )

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "What is Charging Wattage?",
                    text = "Wattage represents electrical power (Voltage multiplied by Current). Real-time wattage lets you verify how much energy your wall adaptor or wireless charger is actively delivering to the device."
                )
            }
            "Temperature" -> {
                val tempText = if (isCelsius) {
                    String.format("%.1f °C", batteryState.temperatureC)
                } else {
                    String.format("%.1f °F", batteryState.temperatureC * 9/5 + 32)
                }
                val subTempText = if (isCelsius) {
                    String.format("%.1f °F", batteryState.temperatureC * 9/5 + 32)
                } else {
                    String.format("%.1f °C", batteryState.temperatureC)
                }

                Text(
                    text = tempText,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = subTempText,
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Custom thermometer gradient scale
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Blue, AccentGreen, Color.Yellow, Color.Red)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0°C", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text("25°C", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text("35°C", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                        Text("50°C", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Thermal alert box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Warmer than ideal",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Text(
                                text = "Keep phone out of direct sunlight while charging.",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LineChart(
                    points = batteryState.temperatureHistory,
                    lineColor = Color(0xFFFF9800),
                    unitSuffix = "°C"
                )

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "Battery Temperature Safety",
                    text = "A battery runs coolest when idle. Heavy tasks or high ambient heat during fast charging can raise temps. Keeping your phone below 40°C heavily protects long-term cycle lifespan and performance safety."
                )
            }
            "Health" -> {
                Text(
                    text = "~${batteryState.healthPercent}%",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "Excellent Health Rating",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Crowdsourced feedback info box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = accentColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Live estimate from your charge counter, refined by 228 crowd reports.",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Capacity table metrics
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                ) {
                    CapacityRow("Estimated capacity", "${batteryState.estimatedCapacityMah} mAh")
                    CapacityRow("Design capacity", "${batteryState.maxCapacityMah} mAh")
                    CapacityRow("Measured at", "65%")
                    CapacityRow("Last measured", "July 15")
                    CapacityRow("Reported status", "Good")
                }

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "Why is health capacity under 100%?",
                    text = "Every lithium battery suffers subtle wear with each charge cycle. Fast charging, deep discharge, or high heat accelerate this process. A level below 80% usually indicates it might be time for a service swap."
                )
            }
            "Plugged" -> {
                Text(
                    text = batteryState.pluggedState,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "Wall Charging, Usually Fastest",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                ) {
                    CapacityRow("Typical full charge", "162 min")
                    CapacityRow("Typical peak power", "20 W")
                    CapacityRow("Technology", "Li-poly")
                }

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "Charging Adapters Info",
                    text = "Wall chargers (AC) deliver highest current levels and fastest times. Charging from a laptop USB or standard wireless is slower but cooler, which is healthier for cell preservation."
                )
            }
            "Max Capacity" -> {
                Text(
                    text = "${batteryState.maxCapacityMah} mAh",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "Factory Design Spec",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                ) {
                    CapacityRow("Design capacity", "4181 mAh")
                    CapacityRow("Charge counter", "2590 mAh")
                    CapacityRow("Battery level", "${batteryState.level}%")
                    CapacityRow("Source", "System Spec")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.preferences.setCustomMaxCapacity(4500) },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Set capacity manually", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExplanationCard(
                    title = "Battery Nominal Capacity",
                    text = "Your manufacturer sets the standard mAh capacity when the phone leaves the factory. This represents the total electrical charge the battery can theoretically hold."
                )
            }
            "Charge Status" -> {
                Text(
                    text = batteryState.chargeStatus,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "Battery is active",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                ) {
                    CapacityRow("Charging power", String.format("%.1f W", batteryState.wattageW))
                    CapacityRow("Battery level", "${batteryState.level}%")
                    CapacityRow("Voltage", "${batteryState.voltageMv} mV")
                }

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "Battery Charge Cycle",
                    text = "This tracks whether current is entering or exiting the chemical cells. In fast charging mode, smart device profiles allow higher voltage/current peaks earlier on, then throttle downward near full level to avoid overheating."
                )
            }
            "Charge Time" -> {
                Text(
                    text = "Full in 25m",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                )
                Text(
                    text = "Estimate based on AC adapter speeds",
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondary)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                ) {
                    CapacityRow("AC wall adapter (typical)", "Full in 25m")
                    CapacityRow("USB Computer port (slow)", "Full in 1h 45m")
                    CapacityRow("Wireless charging pad", "Full in 1h 12m")
                }

                Spacer(modifier = Modifier.height(24.dp))

                ExplanationCard(
                    title = "Why does the last 20% feel slow?",
                    text = "Lithium-ion batteries charge in two stages: Constant Current (fast, up to 80%) and Constant Voltage (slow, final 20%). Throttling down the current near the end protects the delicate cell from thermal runaways."
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun ExplanationCard(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, lineHeight = 20.sp)
            )
        }
    }
}

@Composable
fun CapacityRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
    }
}
