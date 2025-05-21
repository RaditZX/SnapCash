package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var isIncomeMode by remember { mutableStateOf(false) }
    val chartItemsOutcome = listOf(
        ChartItem("Pendidikan", 1952000f, Color(0xFFF53844)),
        ChartItem("Belanja", 7244000f, Color(0xFF2D6CE9)),
        ChartItem("Transportasi", 3216000f, Color(0xFF20BF55)),
        ChartItem("Hiburan", 16000000f, Color(0xFFFFA500)),
        ChartItem("Extra", 0f, Color(0xFFFFA500))
    )
    val chartItemsIncome = listOf(
        ChartItem("Gaji", 72448000f, Color(0xFF2D6CE9)),
        ChartItem("Investasi", 1952000f, Color(0xFFF53844)),
        ChartItem("Bisnis", 3216000f, Color(0xFF20BF55)),
        ChartItem("BONUS", 16000000f, Color(0xFFFFA500)),
        ChartItem("Extra", 16000000f, Color(0xFFFFA500))
    )

    val itemsPerPage = 3
    val pages = if (isIncomeMode) chartItemsIncome.chunked(itemsPerPage) else chartItemsOutcome.chunked(itemsPerPage)

    val pagerState = rememberPagerState(pageCount = { pages.size })

    val allPointsData = if (isIncomeMode) listOf(
        Point(0f, 800000f),
        Point(1f, 1280000f),
        Point(2f, 320000f),
        Point(3f, 1120000f),
        Point(4f, 640000f),
        Point(5f, 1440000f),
        Point(6f, 960000f)
    ) else listOf(
        Point(0f, 640000f),
        Point(1f, 1440000f),
        Point(2f, 160000f),
        Point(3f, 960000f),
        Point(4f, 480000f),
        Point(5f, 1120000f),
        Point(6f, 800000f)
    )

    val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = allPointsData,
                    lineStyle = LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        width = 2.dp.value,
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
        Text(
            text = "Welcome Back USER",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        var expanded by remember { mutableStateOf(false) }
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isIncomeMode) "Money Income" else "Money Outcome",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.White
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Money Income") },
                    onClick = {
                        isIncomeMode = true
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Money Outcome") },
                    onClick = {
                        isIncomeMode = false
                        expanded = false
                    }
                )
            }
        }

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
                text = "2020",
                color = Color.Magenta,
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
                Text(
                    text = if (isIncomeMode) "MONEY EARNED" else "MONEY SPENT",
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    text = if (isIncomeMode) "Rp165.520.000" else "Rp165.520.000",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "COMPARISON TO LAST YEAR",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = if (isIncomeMode) "Rp158.480.000" else "Rp158.480.000",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (isIncomeMode) "+Rp7.040.000" else "+Rp7.040.000",
                color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = if (isIncomeMode) "Progress Earned Charts" else "Progress Spent Charts",
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
                        total = 160000000f,
                        color = item.color,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(itemsPerPage - currentPageItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Text(
            text = if (isIncomeMode) "Money Earned (Day)" else "Money Spent (Day)",
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
                    Column {
                        Text(
                            text = "Highest: ",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isIncomeMode) "Rp1,440,000" else "Rp1,440,000",
                            color =  if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "Today: ",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isIncomeMode) "Rp320,000" else "Rp160,000", // Sesuai hari ini (Rabu)
                            color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Outcome Summary Section
        Text(
            text = if (isIncomeMode) "INCOME SUMMARY" else "OUTCOME SUMMARY",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
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
                    text = if (isIncomeMode) "+Rp14,368,000" else "-Rp14,368,000",
                    color = if (isIncomeMode)
                                Color.Green
                            else
                                Color(0xFFFF1E00),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text("CEDINT RENT APARTMENT", color = Color.White, fontSize = 14.sp)
                Text(
                    text = if (isIncomeMode) "+Rp7,969,600" else "-Rp7,969,600",
                    color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("RESTAURANT BBQ", color = Color.White, fontSize = 14.sp)
                Text(
                    text = if (isIncomeMode) "+Rp2,830,400" else "-Rp2,830,400",
                    color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("FOOD", color = Color.White, fontSize = 14.sp)
                Text(
                    text = if (isIncomeMode) "+Rp1,692,160" else "-Rp1,692,160",
                    color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("ELECTRIC CAR", color = Color.White, fontSize = 14.sp)
                Text(
                    text = if (isIncomeMode) "+Rp531,200" else "-Rp531,200",
                    color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("DRINKS AND DISCO PARTY", color = Color.White, fontSize = 14.sp)
                Text(
                    text = if (isIncomeMode) "+Rp531,200" else "-Rp531,200",
                    color = if (isIncomeMode) Color.Green else Color(0xFFFF1E00),
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class ChartItem(
    val label: String,
    val value: Float,
    val color: Color
)