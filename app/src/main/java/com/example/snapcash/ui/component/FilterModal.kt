package com.example.snapcash.ui.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.CategoryViewModel
import com.example.snapcash.data.FilterModel
import com.example.snapcash.data.Transaction
import com.example.snapcash.ui.theme.night
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    isPemasukan: Boolean = false,
    filterData: (FilterModel) -> Unit,
    navController: NavController,
    dataTransaction: List<Transaction>,
    periode: String
) {
    var range by remember { mutableStateOf(0f..20000000000f) }
    var minValue by remember { mutableStateOf(range.start.toInt()) }
    var maxValue by remember { mutableStateOf(range.endInclusive.toInt()) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var kategori by remember { mutableStateOf("") }
    val kategoriPemasukanList = listOf("Gaji", "Investasi", "Bisnis", "Hadiah")
    val kategoriPengeluaranList = listOf("Transportasi", "Belanja", "Pendidikan", "Hiburan")
    categoryViewModel.getAllCategories()
    val defaultList = if (isPemasukan) {
        kategoriPemasukanList
    } else {
        kategoriPengeluaranList
    }

    val categories by categoryViewModel.categories
    val filteredFromDb = categories
        .filter { if (isPemasukan) !it.isPengeluaran else it.isPengeluaran }
        .map { it.nama }

    val allCategories = (defaultList + filteredFromDb).distinct()


    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .background(MaterialTheme.colorScheme.outline, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Filter", color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    minValue = range.start.toInt()
                    maxValue = range.endInclusive.toInt()
                    startDate = ""
                    endDate = ""
                    showStartDatePicker = false
                    showEndDatePicker = false
                    kategori = ""
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh, // atau Icons.Rounded.Restore
                        contentDescription = "Reset Filter",
                        tint = Color(0xFF2D6CE9)
                    )
                }

            }

            Spacer(Modifier.height(30.dp))

            Row {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    label = { Text("Start Date", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select start date", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    label = { Text("End Date", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select end date", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            if (showStartDatePicker) {
                DatePickerDialogExample(onDatePicked = { selectedDate ->
                    startDate = selectedDate
                    showStartDatePicker = false
                }, onDismiss = { showStartDatePicker = false })
            }

            if (showEndDatePicker) {
                DatePickerDialogExample(onDatePicked = { selectedDate ->
                    endDate = selectedDate
                    showEndDatePicker = false
                }, onDismiss = { showEndDatePicker = false })
            }

            Spacer(Modifier.height(40.dp))

            Text("Nominal", color = MaterialTheme.colorScheme.onSurface)

            RangeSlider(
                value = range,
                onValueChange = {
                    range = it
                    minValue = it.start.toInt()
                    maxValue = it.endInclusive.toInt()
                },
                valueRange = 0f..20000000f,
                steps = 200,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.onSurface,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    activeTickColor = MaterialTheme.colorScheme.primary
                )
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Min", color = MaterialTheme.colorScheme.onSurface)
                    CurrencyInputField(
                        label = "Min",
                        value = minValue,
                        onValueChange = { minValue = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Max", color = MaterialTheme.colorScheme.onSurface)
                    CurrencyInputField(
                        label = "Max",
                        value = maxValue,
                        onValueChange = { maxValue = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column {
                Text("Kategori", color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(10.dp))
                DropdownMenu(
                    containerColor = MaterialTheme.colorScheme.surface,
                    label = "Kategori",
                    options = allCategories,
                    selectedOption = kategori,
                    onOptionSelected = { kategori = it }
                )
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val filterModel = FilterModel(
                        min = minValue,
                        max = maxValue,
                        startDate = startDate,
                        endDate = endDate,
                        kategori = kategori
                    )
                    filterData(filterModel)
                    onDismiss()
                    navController.navigate("history")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Filter", color = MaterialTheme.colorScheme.onPrimary)
            }

            ExportPdfButton(dataTransaction, LocalContext.current, periode, isPemasukan)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun DatePickerDialogExample(
    onDatePicked: (String) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateTimeFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("id", "ID"))

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        val formattedDate = dateTimeFormatter.format(calendar.time)
                        onDatePicked(formattedDate)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { onDismiss() }
            setOnDismissListener { onDismiss() }
        }
    }

    LaunchedEffect(datePickerDialog) {
        datePickerDialog.show()
    }
}








