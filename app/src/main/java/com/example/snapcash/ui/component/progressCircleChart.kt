package com.example.snapcash.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressCircleChart(
    label: String,
    value: Float,
    total: Float = 5000f, // Total maksimum untuk persentase (dapat disesuaikan)
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = (value / total).coerceIn(0f, 1f) // Pastikan progress antara 0 dan 1
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2, canvasHeight / 2)
            val radius = canvasHeight.coerceAtMost(canvasWidth) / 2 * 0.7f

            // Background circle (abu-abu)
            drawArc(
                color = Color.Gray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 20f)
            )

            // Progress circle
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 20f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$$value",
            color = Color.White,
            fontSize = 14.sp
        )
    }
}