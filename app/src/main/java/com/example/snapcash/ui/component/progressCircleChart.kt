package com.example.snapcash.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.foundation.layout.size

@Composable
fun ProgressCircleChart(
    label: String,
    value: Float,
    total: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (total == 0f) 0f else value / total

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CircularProgressIndicator(
            progress = progress,
            color = color,
            strokeWidth = 8.dp,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White)
        Text("Rp ${value.toInt()}", color = Color.White, fontSize = 12.sp)
    }
}