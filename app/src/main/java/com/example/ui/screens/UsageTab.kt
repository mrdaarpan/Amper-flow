package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel
import kotlin.math.sin

@Composable
fun UsageTab(
    viewModel: AmpereFlowViewModel,
    modifier: Modifier = Modifier
) {
    val batteryState by viewModel.batteryState.collectAsState()
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
            text = "USAGE CHARTS & ANALYTICS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Charging speed benchmark card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHARGING SPEED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Great", color = accentColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val deviceManufacturer = android.os.Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                Text(
                    text = "Faster than 99% of $deviceManufacturer devices",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UsageStat(label = "AVG SPEED", value = "5W")
                    UsageStat(label = "PEAK SPEED", value = "9W")
                    UsageStat(label = "YOUR DEVICE", value = "16W")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Charging Power Level segments (horizontal bar series)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CHARGING POWER BY LEVEL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PowerLevelBar(range = "0-10%", avg = 3f, peak = 8f)
                    PowerLevelBar(range = "10-20%", avg = 6f, peak = 25f)
                    PowerLevelBar(range = "20-30%", avg = 5f, peak = 12f)
                    PowerLevelBar(range = "30-40%", avg = 8f, peak = 23f)
                    PowerLevelBar(range = "40-50%", avg = 6f, peak = 22f)
                    PowerLevelBar(range = "50-60%", avg = 5f, peak = 11f)
                    PowerLevelBar(range = "60-70%", avg = 5f, peak = 9f)
                    PowerLevelBar(range = "70-80%", avg = 5f, peak = 9f)
                    PowerLevelBar(range = "80-90%", avg = 6f, peak = 10f)
                    PowerLevelBar(range = "90-100%", avg = 4f, peak = 9f)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Charging Profile Dual Axis Curve
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CHARGING PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(accentColor))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Charging speed", style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary))
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF9800)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Temperature", style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Canvas custom Dual-Curve Graph
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(vertical = 12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Axis lines
                        drawLine(color = Color.Gray.copy(alpha = 0.2f), start = Offset(0f, height), end = Offset(width, height))
                        drawLine(color = Color.Gray.copy(alpha = 0.2f), start = Offset(0f, 0f), end = Offset(0f, height))

                        // Generate beautiful sinusoidal curves over 0-100% battery index
                        val speedPath = Path()
                        val tempPath = Path()

                        val segments = 50
                        for (i in 0..segments) {
                            val ratio = i.toFloat() / segments
                            val x = ratio * width

                            // Charging speed falls as battery level approaches full (standard curves)
                            val speedW = 18f * (1f - ratio) + 2f + sin(ratio * 12f) * 3f
                            val speedY = height - (speedW / 25f).coerceIn(0f, 1f) * height

                            // Temp rises up to 30%, then stays hot, then cools down near full charge
                            val tempC = 35f + sin(ratio * Math.PI.toFloat()) * 4.5f
                            val tempNormalized = (tempC - 30f) / 15f
                            val tempY = height - tempNormalized.coerceIn(0f, 1f) * height

                            if (i == 0) {
                                speedPath.moveTo(x, speedY)
                                tempPath.moveTo(x, tempY)
                            } else {
                                speedPath.lineTo(x, speedY)
                                tempPath.lineTo(x, tempY)
                            }
                        }

                        // Draw Speed Curve
                        drawPath(path = speedPath, color = accentColor, style = Stroke(width = 3.dp.toPx()))
                        // Draw Temp Curve
                        drawPath(path = tempPath, color = Color(0xFFFF9800), style = Stroke(width = 3.dp.toPx()))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0%", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Text("25%", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Text("50%", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Text("75%", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                    Text("100%", style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Peaks around 35.4°C at 3% battery level",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 10.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun UsageStat(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextSecondary))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
    }
}
