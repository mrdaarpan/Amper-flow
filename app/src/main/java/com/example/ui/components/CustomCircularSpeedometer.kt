package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CustomCircularSpeedometer(
    percentage: Float,
    statusText: String,
    subText: String,
    styleName: String,
    accentColor: Color,
    brightness: Float,
    modifier: Modifier = Modifier
) {
    // Animate percentage sweeps smoothly
    val animatedPercent by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000),
        label = "percentage_sweep"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(240.dp)
    ) {
        // Core gauge canvas drawing
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .alpha(brightness.coerceIn(0.2f, 1.0f))
        ) {
            val strokeWidth = 14.dp.toPx()
            val sizePx = size.width
            val radius = (sizePx - strokeWidth) / 2
            val centerOffset = Offset(size.width / 2f, size.height / 2f)

            val startAngle = 135f
            val sweepAngle = 270f
            val activeSweep = (animatedPercent / 100f) * sweepAngle

            // Define modern background color
            val bgTrackColor = Color(0xFF1E2220)

            when (styleName) {
                "Full Arc" -> {
                    // Draw continuous track background
                    drawArc(
                        color = bgTrackColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Draw active sweep track with a beautiful gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to accentColor.copy(alpha = 0.5f),
                            1.0f to accentColor
                        ),
                        startAngle = startAngle,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                "Broken Arc" -> {
                    val segments = 8
                    val gap = 6f
                    val totalDegrees = sweepAngle
                    val segDegrees = (totalDegrees - (gap * (segments - 1))) / segments

                    for (i in 0 until segments) {
                        val currentSegStart = startAngle + i * (segDegrees + gap)
                        val segmentCenter = currentSegStart + (segDegrees / 2)
                        
                        // Background track segment
                        drawArc(
                            color = bgTrackColor,
                            startAngle = currentSegStart,
                            sweepAngle = segDegrees,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Calculate active segment portion
                        val segmentEndAngle = currentSegStart + segDegrees
                        val currentActiveEndAngle = startAngle + activeSweep

                        if (currentActiveEndAngle > currentSegStart) {
                            val activeSegSweep = (currentActiveEndAngle - currentSegStart).coerceAtMost(segDegrees)
                            drawArc(
                                color = accentColor,
                                startAngle = currentSegStart,
                                sweepAngle = activeSegSweep,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }
                }
                "Dash Arc" -> {
                    // Dotted/dashed stroke style
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    
                    drawArc(
                        color = bgTrackColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                    )
                    drawArc(
                        color = accentColor,
                        startAngle = startAngle,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, pathEffect = pathEffect)
                    )
                }
                "Tick Arc" -> {
                    // Draw beautiful ticks using manual trigonometry lines
                    val numTicks = 45
                    val innerRadius = radius - strokeWidth
                    val outerRadius = radius + 6.dp.toPx()

                    for (i in 0..numTicks) {
                        val tickAngle = startAngle + (i.toFloat() / numTicks) * sweepAngle
                        val angleRad = Math.toRadians(tickAngle.toDouble())

                        val cosA = cos(angleRad).toFloat()
                        val sinA = sin(angleRad).toFloat()

                        val startX = centerOffset.x + innerRadius * cosA
                        val startY = centerOffset.y + innerRadius * sinA
                        val endX = centerOffset.x + outerRadius * cosA
                        val endY = centerOffset.y + outerRadius * sinA

                        val isActive = (i.toFloat() / numTicks) * 100f <= animatedPercent
                        val tickColor = if (isActive) accentColor else bgTrackColor

                        drawLine(
                            color = tickColor,
                            start = androidx.compose.ui.geometry.Offset(startX, startY),
                            end = androidx.compose.ui.geometry.Offset(endX, endY),
                            strokeWidth = if (isActive) 3.dp.toPx() else 2.dp.toPx()
                        )
                    }
                }
                else -> { // Simple Thin Ring style
                    drawArc(
                        color = bgTrackColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    drawArc(
                        color = accentColor,
                        startAngle = 270f,
                        sweepAngle = (animatedPercent / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        // Inside layout text metrics (Big percentage, charging label, wattage/mW)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.2f%%", percentage),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = statusText.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = accentColor
                )
            )
            if (subText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                androidx.compose.material3.Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = subText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
                }
            }
        }
    }
}
