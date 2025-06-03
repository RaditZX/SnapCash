package com.example.snapcash.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressCircleChart(
    label: String,
    value: Float,
    total: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (total == 0f) 0f else value / total
    val strokeWidth = 8.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(72.dp)
        ) {
            Canvas(modifier = Modifier.size(72.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val strokeWidthPx = strokeWidth.toPx()
                val radius = (minOf(canvasWidth, canvasHeight) - strokeWidthPx) / 2
                val center = androidx.compose.ui.geometry.Offset(
                    canvasWidth / 2f,
                    canvasHeight / 2f
                )

                // Draw background circle (abu-abu gelap)
                drawCircle(
                    color = Color(0xFF2A2A2A),
                    radius = radius,
                    center = center,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                )

                // Draw progress arc
                if (progress > 0f) {
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(
                            center.x - radius,
                            center.y - radius
                        ),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        style = Stroke(
                            width = strokeWidthPx,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White)
        Text(formatCurrency(value.toInt()), color = Color.White, fontSize = 12.sp)
    }
}