package com.example.snapcash.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
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
import kotlin.reflect.KFunction4

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    viewModel1: AuthViewModel = hiltViewModel(),
) {
    val userData by remember { viewModel1.userDatas }
    val tahun = Calendar.getInstance().get(Calendar.YEAR)
    val dashboardData by viewModel.dashboardData
    val isLoading by viewModel.isLoading
    var isIncomeMode by remember { mutableStateOf(false) }
    var incomeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isIncomeMode){
            viewModel.getDashboardAnalytics(tahun = tahun, jenis = "Pemasukan")
        }else{
            viewModel.getDashboardAnalytics(tahun = tahun, jenis = "Pengeluaran")
        }

        viewModel1.getUserData()
    }


    var showFilterDialog by remember { mutableStateOf(false) }
    var tempFilter by remember { mutableStateOf("Year") }
    var tempValue by remember { mutableStateOf(tahun.toString()) }

    val chartItems = dashboardData?.TotalByKategori?.map { (key, value) ->
        ChartItem(key, value.toFloat(), color = generateBrightRandomColor())
    } ?: emptyList()

    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var selectedFilter by remember { mutableStateOf("tahun") }
    var selectedValue by remember { mutableStateOf(tahun.toString()) }
    var selectedFilterTranslate by remember { mutableStateOf("Year") }

    if (selectedFilter === "bulan"){
        selectedFilterTranslate = "MONTH"
    }
    if (selectedFilter === "hari"){
        selectedFilterTranslate = "DAY"
    }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val years = (tahun - 4..tahun).toList().reversed()
    val days = getDays(selectedFilter, selectedValue, tahun, months)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Welcome Back ${userData.username.toString()}",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        var incomeExpanded by remember { mutableStateOf(false) }
        Box {
            EnhancedIncomeDropdown(
                expanded = incomeExpanded,
                onExpandedChange = { incomeExpanded = it },
                isIncomeMode = isIncomeMode,
                onModeChanged = { newMode ->
                    isIncomeMode = newMode
                    // Call your update function here
                    Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
                    Log.d("income", isIncomeMode.toString())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )
        }

        EnhancedFilterButton(
            selectedFilter = selectedFilter,
            selectedValue = selectedValue,
            onFilterChanged = { filter, value ->
                selectedFilter = filter
                selectedValue = value.toString()
                // Call your update function
                Updatebyfilter(viewModel, isIncomeMode, selectedFilter, selectedValue, months, tahun)
            },
            years = years, // List<Int>
            months = months, // List<Int> (1-12)
            getDays = ::getDays, // Function that returns List<Int>
            tahun = tahun, // Int
            modifier = Modifier.padding(bottom = 10.dp)
        )


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
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = formatCurrency(dashboardData?.total ?: 0),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text("COMPARISON TO LAST ${selectedFilterTranslate}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 2.dp))
                Text(formatCurrency(dashboardData?.totalTahunSebelumnya ?: 0), color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = formatCurrency(dashboardData?.perubahanTotal ?: 0),
                color = if (isIncomeMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Progress Chart
        Text(
            text = if (isIncomeMode) "Progress Earned Charts" else "Progress Spent Charts",
            color = MaterialTheme.colorScheme.onBackground,
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
            text = if (isIncomeMode) "Money Earned (${selectedFilterTranslate})" else "Money Spent (${selectedFilterTranslate})",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(2.dp, Brush.linearGradient(listOf(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f), MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), Color.Transparent)), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background.copy(alpha = 1f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

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
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formatCurrency(amount),
                    color = MaterialTheme.colorScheme.onBackground,
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

fun generateBrightRandomColor(): Color {
    val hue = Random.nextFloat() * 360f       // Hue 0 - 360
    val saturation = 0.9f                     // High saturation
    val lightness = 0.6f                      // Light color (bright enough)

    val colorInt = ColorUtils.HSLToColor(floatArrayOf(hue, saturation, lightness))
    return Color(colorInt)
}

@Composable
fun EnhancedIncomeDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isIncomeMode: Boolean,
    onModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Main dropdown button
        OutlinedButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (expanded) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (expanded) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icon based on mode
                    Icon(
                        imageVector = if (isIncomeMode) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (isIncomeMode) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(24.dp)
                    )

                    // Current selection text
                    Text(
                        text = if (isIncomeMode) "Money Income" else "Money Outcome",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Dropdown arrow
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = "Expand dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (expanded) 0f else 0f)
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                ),
            offset = DpOffset(0.dp, 8.dp)
        ) {
            // Income option
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Money Income",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Track your earnings",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                onClick = {
                    onModeChanged(true)
                    onExpandedChange(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isIncomeMode) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else Color.Transparent
                    ),
                leadingIcon = if (isIncomeMode) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )

            // Divider
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Outcome option
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Money Outcome",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Track your expenses",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                onClick = {
                    onModeChanged(false)
                    onExpandedChange(false)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (!isIncomeMode) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else Color.Transparent
                    ),
                leadingIcon = if (!isIncomeMode) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
    }
}

@Composable
fun EnhancedFilterButton(
    selectedFilter: String,
    selectedValue: Any, // Changed from Int to Any to handle both Int and String
    onFilterChanged: (String, Any) -> Unit, // Changed from Int to Any
    years: List<Int>,
    months: List<String>,
    getDays: KFunction4<String, String, Int, List<String>, List<Int>>, // Changed second parameter to Any
    tahun: Int,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    // Filter button
    OutlinedButton(
        onClick = { showFilterDialog = true },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Filter: $selectedFilter",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = getDisplayValue(selectedFilter, selectedValue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open filter",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Enhanced Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            selectedFilter = selectedFilter,
            selectedValue = selectedValue,
            onDismiss = { showFilterDialog = false },
            onApply = { filter, value ->
                onFilterChanged(filter, value)
                showFilterDialog = false
            },
            years = years,
            months = months,
            getDays = getDays,
            tahun = tahun
        )
    }
}

@Composable
private fun FilterDialog(
    selectedFilter: String,
    selectedValue: Any,
    onDismiss: () -> Unit,
    onApply: (String, Any) -> Unit,
    years: List<Int>,
    months: List<String>,
    getDays: KFunction4<String, String, Int, List<String>, List<Int>>,
    tahun: Int
) {
    var tempFilter by remember { mutableStateOf(selectedFilter) }
    var tempValue by remember { mutableStateOf(selectedValue) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Filter",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Filter Type Selection
                FilterTypeSection(
                    selectedFilter = tempFilter,
                    onFilterChanged = { newFilter ->
                        tempFilter = newFilter
                        tempValue = when (newFilter) {
                            "tahun" -> tahun
                            "bulan" -> "January"
                            "hari" -> 1
                            else -> tahun
                        }
                    }
                )

                // Value Selection
                FilterValueSection(
                    filter = tempFilter,
                    selectedValue = tempValue,
                    onValueChanged = { tempValue = it },
                    years = years,
                    months = months,
                    getDays = getDays,
                    tahun = tahun
                )

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onApply(tempFilter, tempValue) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Filter")
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterTypeSection(
    selectedFilter: String,
    onFilterChanged: (String) -> Unit
) {
    var filterExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Filter Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Box {
            OutlinedButton(
                onClick = { filterExpanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (filterExpanded) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(
                    1.dp,
                    if (filterExpanded) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = when (selectedFilter) {
                                "tahun" -> Icons.Default.DateRange
                                "bulan" -> Icons.Default.DateRange
                                "hari" -> Icons.Default.DateRange
                                else -> Icons.Default.DateRange
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = selectedFilter.replaceFirstChar { it.uppercaseChar() },
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = if (filterExpanded) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            DropdownMenu(
                expanded = filterExpanded,
                onDismissRequest = { filterExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                offset = DpOffset(0.dp, 8.dp)
            ) {
                val filterOptions = listOf(
                    Triple("tahun", "Year", Icons.Default.DateRange),
                    Triple("bulan", "Month", Icons.Default.DateRange),
                    Triple("hari", "Day", Icons.Default.DateRange)
                )

                filterOptions.forEachIndexed { index, (filter, label, icon) ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        },
                        onClick = {
                            onFilterChanged(filter)
                            filterExpanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selectedFilter == filter)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                else Color.Transparent
                            ),
                        leadingIcon = if (selectedFilter == filter) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )

                    if (index < filterOptions.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterValueSection(
    filter: String,
    selectedValue: Any,
    onValueChanged: (Any) -> Unit,
    years: List<Int>,
    months: List<String>,
    getDays: KFunction4<String, String, Int, List<String>, List<Int>>,
    tahun: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Select Value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        ) {
            when (filter) {
                "tahun" -> {
                    // List layout for years
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(years) { year ->
                            val isSelected = year == selectedValue
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                ),
                                onClick = { onValueChanged(year) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = year.toString(),
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                "bulan" -> {
                    // List layout for months
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(months) { month ->
                            val isSelected = month == selectedValue
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                ),
                                onClick = { onValueChanged(month) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = month,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                "hari" -> {
                    val days = getDays(filter, selectedValue.toString(), tahun, months)
                    // Grid layout for days
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(days) { day ->
                            val isSelected = day == selectedValue
                            Card(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(2.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surface
                                ),
                                onClick = { onValueChanged(day) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to display values properly
private fun getDisplayValue(filter: String, value: Any): String {
    return when (filter) {
        "tahun" -> value.toString()
        "bulan" -> value.toString() // Already a string from months list
        "hari" -> value.toString()
        else -> value.toString()
    }
}