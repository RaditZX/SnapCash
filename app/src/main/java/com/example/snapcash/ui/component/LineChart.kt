package com.example.snapcash.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.LineChartData

@Composable
fun LineChart(
    modifier: Modifier,
    lineChartData: LineChartData,
    allPointsData: List<Point>,
    allDays: List<String>
) {
    val scrollState = rememberScrollState()
    val dayWidthPx = 100.dp // Lebar setiap hari (sesuai axisStepSize)

    // Hitung offset label hari berdasarkan posisi scroll
    val labelOffset by remember {
        derivedStateOf {
            -(scrollState.value).toFloat()
        }
    }

    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .horizontalScroll(scrollState, enabled = false) // Sinkronkan scroll, tapi non-interaktif
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .offset(x = labelOffset.dp) // Geser label sesuai scroll
            ) {
                allDays.forEach { day ->
                    Text(
                        text = day,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .width(dayWidthPx) // Lebar setiap label sama dengan axisStepSize
                            .wrapContentHeight(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Chart dengan Scroll Horizontal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds() // Pastikan konten tidak terpotong secara visual
                .horizontalScroll(scrollState)
        ) {
            val updatedLineChartData = lineChartData.copy(
                linePlotData = lineChartData.linePlotData.copy(
                    lines = lineChartData.linePlotData.lines.map { line ->
                        line.copy(dataPoints = allPointsData)
                    }
                ),
                xAxisData = lineChartData.xAxisData.copy(
                    labelData = { "" }, // Kosongkan label sumbu X
                    axisLineColor = Color.Transparent,
                    axisLabelColor = Color.Transparent,
                    shouldDrawAxisLineTillEnd = false,
                    steps = allPointsData.size - 1, // Jumlah langkah untuk garis vertikal
                    axisStepSize = dayWidthPx
                ),
                yAxisData = lineChartData.yAxisData.copy(
                    labelData = { "" }, // Kosongkan label sumbu Y
                    axisLineColor = Color.Transparent,
                    axisLabelColor = Color.Transparent,
                    shouldDrawAxisLineTillEnd = false
                ),
                gridLines = GridLines(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    enableVerticalLines = true,
                    enableHorizontalLines = false
                )
            )

            LineChart(
                modifier = Modifier
                    .width(dayWidthPx * allPointsData.size) // Lebar total chart sesuai jumlah hari
                    .height(200.dp),
                lineChartData = updatedLineChartData
            )
        }
    }
}