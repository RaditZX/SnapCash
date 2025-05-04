package com.example.snapcash.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartData
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.common.model.PlotType


@Composable
fun DonutChartComponent(
    modifier: Modifier = Modifier
) {
    val donutChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("HP", 15f, Color(0xFF5F0A87)),
            PieChartData.Slice("Dell", 30f, Color(0xFF20BF55)),
            PieChartData.Slice("Lenovo", 40f, Color(0xFFEC9F05)),
            PieChartData.Slice("Asus", 10f, Color(0xFFF53844))
        ),
        plotType = PlotType.Donut
    )

    val donutChartConfig = PieChartConfig(
        strokeWidth = 120f,                 // ukuran lubang donut
        activeSliceAlpha = 0.9f,            // transparansi slice aktif
        isAnimationEnable = true,           // animasi aktif
        labelVisible = true,                // tampilkan label (persentase)
        labelFontSize = 42.sp,              // ukuran teks label
        labelColor = Color.Black,           // warna teks label
    )


    DonutPieChart(
        modifier = modifier
            .fillMaxWidth()
            .height(500.dp),
        pieChartData = donutChartData,
        pieChartConfig = donutChartConfig
    )
}
