package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.AuthViewModel
import com.example.snapcash.ViewModel.DashboardViewModel
import com.example.snapcash.ui.component.LineChartDashboard
import com.example.snapcash.ui.component.ProgressCircleChart
import com.example.snapcash.ui.component.formatCurrency
import java.util.Calendar
import kotlin.random.Random

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    viewModel1: AuthViewModel = hiltViewModel(),
    openSidebar: () -> Unit
) {
    val userData by remember { viewModel1.userDatas }
    val tahun = Calendar.getInstance().get(Calendar.YEAR)
    val dashboardData by viewModel.dashboardData
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.getDashboardAnalytics(tahun = tahun, jenis = "Pengeluaran")
        viewModel1.getUserData()
    }

    var isIncomeMode by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var tempFilter by remember { mutableStateOf("Year") }
    var tempValue by remember { mutableStateOf(tahun.toString()) }

    val chartItems = dashboardData?.TotalByKategori?.map { (key, value) ->
        ChartItem(key, value.toFloat(), color = generateBrightRandomColor())
    } ?: emptyList()

    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var selectedFilter by remember { mutableStateOf("Year") }
    var selectedValue by remember { mutableStateOf(tahun.toString()) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val years = (tahun - 4..tahun).toList().reversed()
    val days = getDays(selectedFilter, selectedValue, tahun, months)

    val valueItems = getValueByFilter(selectedFilter, years, months, days)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(
            onClick = openSidebar,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Sidebar Menu", tint = Color.White)
        }

        Text(
            text = "Welcome Back ${userData.username.toString()}",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        var incomeExpanded by remember { mutableStateOf(false) }
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
                IconButton(onClick = { incomeExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = Color.White
                    )
                }
            }
            DropdownMenu(
                expanded = incomeExpanded,
                onDismissRequest = { incomeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Money Income") },
                    onClick = {
                        isIncomeMode = true
                        Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
                        incomeExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Money Outcome") },
                    onClick = {
                        isIncomeMode = false
                        Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
                        incomeExpanded = false
                    }
                )
            }
        }

        Button(
            onClick = { showFilterDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A2A2A),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Filter: $selectedFilter - $selectedValue",
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Filter Button"
                )
            }
        }

        // Improved Filter Dialog
        if (showFilterDialog) {
            Dialog(
                onDismissRequest = { showFilterDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Select Filter",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Filter Type Selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Filter Type",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            var filterExpanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { filterExpanded = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(tempFilter)
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Filter Dropdown"
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = filterExpanded,
                                    onDismissRequest = { filterExpanded = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf("Year", "Month", "Day").forEach { filter ->
                                        DropdownMenuItem(
                                            text = { Text(filter) },
                                            onClick = {
                                                tempFilter = filter
                                                tempValue = when (filter) {
                                                    "Year" -> tahun.toString()
                                                    "Month" -> "January"
                                                    "Day" -> "1"
                                                    else -> tahun.toString()
                                                }
                                                filterExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Value Selection with improved display
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Select Value",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            val tempValueItems = getValueByFilter(tempFilter, years, months, getDays(tempFilter, tempValue, tahun, months))

                            if (tempFilter == "Day") {
                                // Grid layout for days (1-31)
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(7),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(tempValueItems) { day ->
                                        val isSelected = day == tempValue
                                        Card(
                                            modifier = Modifier
                                                .aspectRatio(1f)
                                                .padding(2.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color(0xFF6200EE) else Color(0xFF2A2A2A)
                                            ),
                                            onClick = { tempValue = day }
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = day,
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // List layout for months and years
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(tempValueItems) { value ->
                                        val isSelected = value == tempValue
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color(0xFF6200EE) else Color(0xFF2A2A2A)
                                            ),
                                            onClick = { tempValue = value }
                                        ) {
                                            Text(
                                                text = value,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showFilterDialog = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    selectedFilter = tempFilter
                                    selectedValue = tempValue
                                    Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
                                    showFilterDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6200EE),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Apply")
                            }
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(200.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (isIncomeMode) "MONEY EARNED" else "MONEY SPENT",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = formatCurrency(dashboardData?.total ?: 0),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text("COMPARISON TO LAST YEAR", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 2.dp))
                Text(formatCurrency(dashboardData?.totalTahunSebelumnya ?: 0), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = formatCurrency(dashboardData?.perubahanTotal ?: 0),
                color = if (isIncomeMode) Color(0xFF00FF00) else Color(0xFFFF1E00),
                fontSize = 20.sp,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            chartItems.forEach { item ->
                ProgressCircleChart(
                    label = item.label,
                    value = item.value,
                    total = dashboardData?.TotalByKategori?.values?.sum()?.toFloat() ?: 1f,
                    color = item.color,
                    modifier = Modifier.width(100.dp)
                )
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
                .border(2.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.2f), Color.Transparent)), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LineChartDashboard(totalByRange = dashboardData?.TotalByRange ?: emptyMap())
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    dashboardData?.TotalByRange?.forEach { (key, value) ->
                        tableChartData(key, value)
                    }
                }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

// ======================
// Utility & Helper Functions
// ======================

@Composable
fun tableChartData(year: String, amount: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = year,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formatCurrency(amount),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun getDays(filter: String, selectedValue: String, tahun: Int, months: List<String>): List<Int> {
    if (filter != "Day") return (1..31).toList()
    val selectedMonth = months.indexOf(selectedValue).takeIf { it >= 0 } ?: 0
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, tahun)
        set(Calendar.MONTH, selectedMonth)
    }
    return (1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).toList()
}

fun getValueByFilter(
    filter: String,
    years: List<Int>,
    months: List<String>,
    days: List<Int>
): List<String> {
    return when (filter) {
        "Year" -> years.map { it.toString() }
        "Month" -> months
        "Day" -> days.map { it.toString() }
        else -> years.map { it.toString() }
    }
}

fun Updatebyfilter(
    viewModel: DashboardViewModel,
    isIncomeMode: Boolean,
    selectedFilter: String,
    selectedValue: String,
    months: List<String>,
    defaultYear: Int
) {
    val jenis = if (isIncomeMode) "Pemasukan" else "Pengeluaran"
    val filter = selectedFilter.lowercase()
    val tahun = if (filter == "year") selectedValue.toIntOrNull() ?: defaultYear else defaultYear
    val bulan = if (filter == "month") months.indexOf(selectedValue) + 1 else 1
    val hari = if (filter == "day") selectedValue.toIntOrNull() ?: 1 else 1

    viewModel.getDashboardAnalytics(
        jenis = jenis,
        filter = filter,
        tahun = tahun,
        bulan = bulan,
        hari = hari
    )
}

data class ChartItem(
    val label: String,
    val value: Float,
    val color: Color
)

fun generateBrightRandomColor(): Color {
    val hue = Random.nextFloat() * 360f       // Hue 0 - 360
    val saturation = 0.9f                     // High saturation
    val lightness = 0.6f                      // Light color (bright enough)

    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    return Color(colorInt)
}