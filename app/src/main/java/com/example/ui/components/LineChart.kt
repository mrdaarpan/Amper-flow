package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LineChart(
    points: List<Float>,
    lineColor: Color,
    unitSuffix: String,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf("10m") }
    val ranges = listOf("1m", "10m", "1h", "6h")

    // Dynamic slice based on selected range
    val displayPoints = remember(points, selectedRange) {
        val count = when (selectedRange) {
            "1m" -> 15
            "10m" -> 30
            "1h" -> 45
            else -> points.size
        }
        points.takeLast(count).ifEmpty { listOf(0f) }
    }

    val maxVal = remember(displayPoints) { (displayPoints.maxOrNull() ?: 100f).coerceAtLeast(1f) }
    val minVal = remember(displayPoints) { (displayPoints.minOrNull() ?: 0f).coerceAtMost(maxVal - 1f) }
    val avgVal = remember(displayPoints) { displayPoints.average().toFloat() }

    Column(modifier = modifier.fillMaxWidth()) {
        // Line chart canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                val width = size.width
                val height = size.height
                val pointCount = displayPoints.size

                val xSpacing = if (pointCount > 1) width / (pointCount - 1) else width
                val valueRange = if (maxVal - minVal > 0) maxVal - minVal else 1f

                // Draw horizontal background grid lines (dashed)
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    val value = maxVal - (valueRange * (i.toFloat() / gridLines))
                    
                    drawLine(
                        color = DarkBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                if (displayPoints.isNotEmpty()) {
                    val strokePath = Path()
                    val fillPath = Path()

                    displayPoints.forEachIndexed { index, value ->
                        val x = index * xSpacing
                        val normalizedY = (value - minVal) / valueRange
                        val y = height - (normalizedY * height)

                        if (index == 0) {
                            strokePath.moveTo(x, y)
                            fillPath.moveTo(x, height)
                            fillPath.lineTo(x, y)
                        } else {
                            strokePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }

                        if (index == displayPoints.lastIndex) {
                            fillPath.lineTo(x, height)
                            fillPath.close()
                        }
                    }

                    // Draw filled surface gradient under the line
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw the core chart line
                    drawPath(
                        path = strokePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Pulse/dot indicator at the latest current point
                    if (pointCount > 0) {
                        val lastX = (pointCount - 1) * xSpacing
                        val lastNormalizedY = (displayPoints.last() - minVal) / valueRange
                        val lastY = height - (lastNormalizedY * height)

                        // Outer breathing ring
                        drawCircle(
                            color = lineColor.copy(alpha = 0.4f),
                            radius = 8.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                        // Core solid dot
                        drawCircle(
                            color = lineColor,
                            radius = 4.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Filter Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ranges.forEach { range ->
                val isSelected = range == selectedRange
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(20))
                        .clickable { selectedRange = range },
                    color = if (isSelected) lineColor else DarkSurface,
                    shape = RoundedCornerShape(20)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = range,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics breakdown cards (Average, Minimum, Maximum)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChartStatBox(
                label = "Average",
                value = String.format("%.0f %s", avgVal, unitSuffix),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ChartStatBox(
                label = "Minimum",
                value = String.format("%.0f %s", minVal, unitSuffix),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ChartStatBox(
                label = "Maximum",
                value = String.format("%.0f %s", maxVal, unitSuffix),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ChartStatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }
    }
}
