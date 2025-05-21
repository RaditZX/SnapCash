package com.example.snapcash.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.ShadowUnderLine

@Composable
fun LineChart(
    modifier: Modifier,
    lineChartData: LineChartData,
    allPointsData: List<Point>,
    allDays: List<String>
) {
        // Chart
        val updatedLineChartData = lineChartData.copy(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = allPointsData,
                        lineStyle = LineStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            width = 2.dp.value,
                            lineType = LineType.SmoothCurve(isDotted = false)
                        ),
                        intersectionPoint = IntersectionPoint(
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        selectionHighlightPoint = SelectionHighlightPoint(
                            color = MaterialTheme.colorScheme.tertiary
                        ),
                        shadowUnderLine = ShadowUnderLine(
                            alpha = 0.5f,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    Color.Transparent
                                )
                            )
                        )
                    )
                )
            ),
            xAxisData = AxisData.Builder()
                .axisStepSize(100.dp)
                .backgroundColor(Color.Transparent)
                .steps(allPointsData.size - 1)
                .labelData { i -> allDays[i] } // Label hari (Senin-Minggu)
                .labelAndAxisLinePadding(15.dp)
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .axisLabelColor(MaterialTheme.colorScheme.tertiary)
                .build(),
            yAxisData = AxisData.Builder()
                .steps(5) // 5 langkah untuk skala 0-100
                .backgroundColor(Color.Transparent)
                .labelData { i ->
                    val yScale = 100 / 5 // Skala pengeluaran (0, 20, 40, 60, 80, 100)
                    (i * yScale).toString()
                }
                .labelAndAxisLinePadding(20.dp)
                .axisLineColor(MaterialTheme.colorScheme.tertiary)
                .axisLabelColor(MaterialTheme.colorScheme.tertiary)
                .build(),
            gridLines = GridLines(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                enableVerticalLines = true,
                enableHorizontalLines = true
            )
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            lineChartData = updatedLineChartData
        )
    }
