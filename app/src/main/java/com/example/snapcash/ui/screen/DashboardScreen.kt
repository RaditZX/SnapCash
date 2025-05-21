package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.snapcash.ui.component.ProgressCircleChart

@Composable
fun DashboardScreen(
    navController: NavController,
    openSidebar: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        IconButton(
            onClick = openSidebar,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Sidebar Menu")
        }
    }

    val itemsPerPage = 3
    val chartItems = listOf(
        ChartItem("Trasport", 122.00f, Color(0xFFF53844)),  // Merah
        ChartItem("Belanja", 4528.00f, Color(0xFF2D6CE9)),    // Biru
        ChartItem("FOOD", 201.00f, Color(0xFF20BF55)),    // Hijau
        ChartItem("EXTRA", 1000.00f, Color(0xFFFFA500))   // Oranye
    )
    val pages = chartItems.chunked(itemsPerPage)

    val pagerState = rememberPagerState(pageCount = { pages.size })

    val allPointsData = listOf(
        Point(0f, 40f),
        Point(1f, 90f),
        Point(2f, 10f),
        Point(3f, 60f),
        Point(4f, 30f),
        Point(5f, 70f),
        Point(6f, 50f),
        Point(7f, 20f),
        Point(8f, 80f),
        Point(9f, 30f),
        Point(10f, 60f),
        Point(11f, 40f)
    )

    // Label hari untuk 12 hari
    val allDays = listOf(
        "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"
    )

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = allPointsData, // Gunakan semua data
                    lineStyle = LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        width = 1.dp.value,
                        lineType = LineType.SmoothCurve(false)
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
        backgroundColor = Color.Transparent,
        xAxisData = AxisData.Builder().build(),
        yAxisData = AxisData.Builder().build(),
        gridLines = GridLines()
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Welcome Message
        Text(
            text = "Welcome Back USER!",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Dropdown Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Money Outcome",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* TODO: Handle dropdown */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.White
                )
            }
        }

        // Statistics Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STATISTICS",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "2023",
                color = Color.Blue,
                fontSize = 16.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text("MONEY SPENT", color = Color.White, fontSize = 20.sp)
                Text("$10,345.00", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("COMPARISON TO LAST YEAR", color = Color.White, fontSize = 16.sp)
                Text("$9,905.00", color = Color.White, fontSize =30.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "+$440",
                color = Color.Red,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Progress Circle Chart Section with HorizontalPager
        Text(
            text = "Progress Circle Charts",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) { page ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentPageItems = pages[page]
                currentPageItems.forEach { item ->
                    ProgressCircleChart(
                        label = item.label,
                        value = item.value,
                        total = 5000f,
                        color = item.color,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Tambahkan Spacer jika kurang dari 3 item untuk menjaga tata letak
                repeat(itemsPerPage - currentPageItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Gradient Area Chart Section
        Text(
            text = "Money Spent (Day)",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
//                LineChartDashboard(
//                    modifier = Modifier.fillMaxWidth(),
//                    lineChartData = lineChartData,
//                    allPointsData = allPointsData,
//                    allDays = allDays
//                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(
                            text = "Pengeluaran Tertinggi: ",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "4500",
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        Text(
                            text = "Pengeluaran Hari Ini: ",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "2500",
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OUTCOME SUMMARY",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "-$898.00",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text("CEDINT RENT APARTMENT", color = Color.White, fontSize = 14.sp)
                Text("-$498.10", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text("RESTAURANT BBQ", color = Color.White, fontSize = 14.sp)
                Text("-$176.90", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text("FOOD", color = Color.White, fontSize = 14.sp)
                Text("-$105.76", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text("ELECTRIC CAR", color = Color.White, fontSize = 14.sp)
                Text("-$33.20", color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text("DRINKS AND DISCO PARTY", color = Color.White, fontSize = 14.sp)
                Text("-$33.20", color = Color.Red, fontSize = 14.sp)
            }
        }
    }
}

data class ChartItem(
    val label: String,
    val value: Float,
    val color: Color
)