package com.example.snapcash.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*

@Composable
fun LineChartDashboard(totalByRange: Map<String, Int>) {
    if (totalByRange.isEmpty()) return

    val labels = totalByRange.keys.toList()
    val pointsData = labels.mapIndexed { index, label ->
        Point(x = index.toFloat(), y = totalByRange[label]?.toFloat() ?: 0f)
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps((pointsData.size - 1).coerceAtLeast(1))
        .labelData { i -> labels.getOrNull(i) ?: "" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisSteps = 5
    val maxY = (pointsData.maxOfOrNull { it.y } ?: 100f).coerceAtLeast(100f)

    val yAxisData = AxisData.Builder()
        .steps(yAxisSteps)
        .backgroundColor(Color.Transparent)
        .labelData { i ->
            val step = (maxY / yAxisSteps).toInt()
            "Rp ${i * step}"
        }
        .labelAndAxisLinePadding(20.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(
                        color = MaterialTheme.colorScheme.primary,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}