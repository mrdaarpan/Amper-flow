package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomCircularSpeedometer
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AmpereFlowViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AodPreviewScreen(
    viewModel: AmpereFlowViewModel,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val batteryState by viewModel.batteryState.collectAsState()

    // Config options
    val clockStyle by viewModel.preferences.clockStyle.collectAsState()
    val speedometerStyle by viewModel.preferences.speedometerStyle.collectAsState()
    val speedometerColorName by viewModel.preferences.speedometerColor.collectAsState()
    val speedometerBrightness by viewModel.preferences.speedometerBrightness.collectAsState()

    // Lock screen elements
    val showBatteryDetails by viewModel.preferences.showBatteryDetails.collectAsState()
    val showWatts by viewModel.preferences.showWatts.collectAsState()
    val percentageAsPrimary by viewModel.preferences.percentageAsPrimary.collectAsState()
    val showMoreInfo by viewModel.preferences.showMoreInfo.collectAsState()
    val showMediaPlayer by viewModel.preferences.showMediaPlayer.collectAsState()
    val showNotifications by viewModel.preferences.showNotifications.collectAsState()
    val torchEnabled by viewModel.preferences.torchEnabled.collectAsState()
    val cameraEnabled by viewModel.preferences.cameraEnabled.collectAsState()

    // Behavior preferences
    val doubleTapExit by viewModel.preferences.doubleTapToExit.collectAsState()
    val oledBurnInProtect by viewModel.preferences.oledBurnInProtection.collectAsState()
    val dateFormatStyle by viewModel.preferences.dateFormat.collectAsState()
    val is24Hour by viewModel.preferences.is24Hour.collectAsState()

    // Interactive media playback state simulation
    var isMusicPlaying by remember { mutableStateOf(true) }
    var currentSong by remember { mutableStateOf("Starlight") }

    // Interactive flashlight simulation state
    var isFlashlightActive by remember { mutableStateOf(false) }

    // Interactive Camera viewfinder simulation overlay state
    var isCameraActive by remember { mutableStateOf(false) }

    // OLED Burn-in pixel protection micro-shifter coordinates
    var xShift by remember { mutableStateOf(0f) }
    var yShift by remember { mutableStateOf(0f) }

    // Ticker for current real time
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    // Ticker for burn-in protection shifts every 20 seconds
    LaunchedEffect(oledBurnInProtect) {
        if (oledBurnInProtect) {
            while (true) {
                delay(12000)
                xShift = (-6..6).random().toFloat()
                yShift = (-6..6).random().toFloat()
            }
        } else {
            xShift = 0f
            yShift = 0f
        }
    }

    val accentColor = when (speedometerColorName.lowercase()) {
        "green" -> AccentGreen
        "purple" -> Color(0xFFBB86FC)
        "indigo" -> Color(0xFF3F51B5)
        "cyan" -> Color(0xFF00E5FF)
        "magenta" -> Color(0xFFFF4081)
        "yellow" -> Color(0xFFFFEB3B)
        else -> AccentGreen
    }

    // Time calculations
    val hourFormatter = SimpleDateFormat(if (is24Hour) "HH" else "h", Locale.getDefault())
    val minFormatter = SimpleDateFormat("mm", Locale.getDefault())
    val fullTimeFormatter = SimpleDateFormat(if (is24Hour) "HH:mm" else "h:mm a", Locale.getDefault())

    val dateObj = Date(currentTimeMillis)
    val hourStr = hourFormatter.format(dateObj)
    val minStr = minFormatter.format(dateObj)
    val timeStr = fullTimeFormatter.format(dateObj)

    val currentFormattedDate = SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(dateObj)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("aod_preview_container")
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (doubleTapExit) {
                            onExit()
                        }
                    },
                    onTap = {
                        // If user hasn't enabled double tap, let single tap trigger a gentle hint
                    }
                )
            }
    ) {
        // OLED Shift container for burn-in protection
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(xShift.dp.roundToPx(), yShift.dp.roundToPx()) }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Upper AOD Clock Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                when (clockStyle) {
                    "6:10" -> {
                        // Stacked bold style
                        Text(
                            text = hourStr,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 80.sp
                            )
                        )
                        Text(
                            text = minStr,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentColor,
                                lineHeight = 80.sp
                            )
                        )
                    }
                    "Digital" -> {
                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 54.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                    "Analog" -> {
                        // Analog dial canvas clock
                        Canvas(modifier = Modifier.size(100.dp)) {
                            val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                            val radius = size.width / 2

                            // Draw outline ring
                            drawCircle(color = Color.DarkGray, radius = radius, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))

                            val hours = dateObj.hours % 12
                            val minutes = dateObj.minutes

                            // Minute hand
                            val minAngle = Math.toRadians((minutes * 6).toDouble() - 90)
                            drawLine(
                                color = Color.White,
                                start = center,
                                end = androidx.compose.ui.geometry.Offset(
                                    (center.x + radius * 0.8 * kotlin.math.cos(minAngle)).toFloat(),
                                    (center.y + radius * 0.8 * kotlin.math.sin(minAngle)).toFloat()
                                ),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Hour hand
                            val hourAngle = Math.toRadians(((hours * 30) + (minutes * 0.5)) - 90)
                            drawLine(
                                color = accentColor,
                                start = center,
                                end = androidx.compose.ui.geometry.Offset(
                                    (center.x + radius * 0.5 * kotlin.math.cos(hourAngle)).toFloat(),
                                    (center.y + radius * 0.5 * kotlin.math.sin(hourAngle)).toFloat()
                                ),
                                strokeWidth = 3.dp.toPx()
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Text(
                    text = currentFormattedDate,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )
            }

            // Middle Speedometer Gauge Block
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                val currentPowerValue = if (showWatts) {
                    String.format("+%.1f W", batteryState.wattageW)
                } else {
                    "+${(batteryState.wattageW * 1000).toInt()} mW"
                }

                CustomCircularSpeedometer(
                    percentage = batteryState.percentageDecimal,
                    statusText = if (batteryState.chargeStatus == "Charging") "Charging" else batteryState.chargeStatus,
                    subText = if (showBatteryDetails) currentPowerValue else "",
                    styleName = speedometerStyle,
                    accentColor = accentColor,
                    brightness = speedometerBrightness
                )
            }

            // Lower Content widgets: Media Player Controls or Notification Icons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (showMediaPlayer) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = currentSong,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Synthesizer Loop",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { currentSong = "Deep Breathe Flow" }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = if (isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { isMusicPlaying = !isMusicPlaying }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = null,
                                    tint = TextPrimary,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { currentSong = "Cosmic Slate Ambient" }
                                )
                            }
                        }
                    }
                }

                if (showNotifications) {
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                        Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                // Interactive exit guidance text
                Text(
                    text = "Double tap to exit preview",
                    modifier = Modifier.alpha(0.4f),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            // Bottom row containing Quick actions (Torch / Camera trigger)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Torch Action button
                if (torchEnabled) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isFlashlightActive) accentColor else Color(0xFF1E2220))
                            .clickable { isFlashlightActive = !isFlashlightActive },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Torch",
                            tint = if (isFlashlightActive) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(50.dp))
                }

                // Camera Action button
                if (cameraEnabled) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2220))
                            .clickable { isCameraActive = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(50.dp))
                }
            }
        }

        // Simulating the camera viewfinder overlay if active
        if (isCameraActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            ) {
                // simulated picture grid or view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Simulated Viewfinder", color = Color.White, fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Camera",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { isCameraActive = false }
                        )
                    }

                    // Centered circular shutter capture action
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(3.dp, Color.Black, CircleShape)
                            .clickable { isCameraActive = false }
                    )
                }
            }
        }

        // Flashlight active simulated overlay indicator
        if (isFlashlightActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.08f)
                    .background(Color(0xFFFFEB3B))
            )
        }
    }
}
