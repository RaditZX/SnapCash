package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.AuthViewModel
import com.example.snapcash.ViewModel.DashboardViewModel
import com.example.snapcash.ui.component.LineChartDashboard
import com.example.snapcash.ui.component.ProgressCircleChart
import com.example.snapcash.ui.component.formatCurrency
import java.util.Calendar

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
    var filter by remember {mutableStateOf(false)}

    LaunchedEffect(Unit) {
        viewModel.getDashboardAnalytics(tahun = tahun, jenis = "Pengeluaran")
        viewModel1.getUserData()
    }



    var isIncomeMode by remember { mutableStateOf(false) }

    val chartItems = dashboardData?.TotalByKategori?.map { (key, value) ->
        ChartItem(key, value.toFloat(), if (isIncomeMode) Color(0xFF00FF00) else Color(0xFFFF1E00))
    } ?: emptyList()

    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var selectedFilter by remember { mutableStateOf("Year") }
    var selectedValue by remember { mutableStateOf(tahun.toString()) }
    var filterExpanded by remember { mutableStateOf(false) }
    var valueExpanded by remember { mutableStateOf(false) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val years = (tahun - 4..tahun).toList().reversed()
    val days = getDays(selectedFilter, selectedValue, tahun, months)

    val valueItems = getValueByFilter(selectedFilter, years, months, days)

//    LaunchedEffect(filter) {
//        Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
//        filter = true;
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F13))
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

        // Dropdown for Income/Outcome
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

        // Filter + Value Dropdowns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Type
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedFilter,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { filterExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Filter Dropdown",
                            tint = Color.White
                        )
                    }
                }
                DropdownMenu(
                    expanded = filterExpanded,
                    onDismissRequest = { filterExpanded = false }
                ) {
                    listOf("tahun", "bulan", "hari").forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter) },
                            onClick = {
                                selectedFilter = filter
                                selectedValue = when (filter) {
                                    "tahun" -> tahun.toString()
                                    "bulan" -> "January"
                                    "hari" -> "1"
                                    else -> tahun.toString()
                                }
                                Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
                                filterExpanded = false
                            }
                        )
                    }
                }
            }

            // Value Type
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedValue,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { valueExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Value Dropdown",
                            tint = Color.White
                        )
                    }
                }
                DropdownMenu(
                    expanded = valueExpanded,
                    onDismissRequest = { valueExpanded = false }
                ) {
                    valueItems.forEach { value ->
                        DropdownMenuItem(
                            text = { Text(value) },
                            onClick = {
                                selectedValue = value
                                Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
                                valueExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Summary Data
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
                    fontSize = 20.sp
                )
                Text(
                    text = formatCurrency(dashboardData?.total ?: 0),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("COMPARISON TO LAST YEAR", color = Color.White, fontSize = 14.sp)
                Text(formatCurrency(dashboardData?.totalTahunSebelumnya ?: 0), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = formatCurrency(dashboardData?.perubahanTotal ?: 0),
                color = if (isIncomeMode) Color(0xFF00FF00) else Color(0xFFFF1E00),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Progress Chart
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

        // Line Chart + Table
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
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 1f)),
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
        "tahun" -> years.map { it.toString() }
        "bulan" -> months
        "hari" -> days.map { it.toString() }
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
    val tahun = if (filter == "tahun") selectedValue.toIntOrNull() ?: defaultYear else defaultYear
    val bulan = if (filter == "bulan") months.indexOf(selectedValue) + 1 else 1
    val hari = if (filter == "hari") selectedValue.toIntOrNull() ?: 1 else 1

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
