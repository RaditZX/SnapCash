package com.example.snapcash.ui.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import com.example.snapcash.data.FilterModel
import com.example.snapcash.ui.theme.night
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    isPemasukan: Boolean = false,
    filterData: (FilterModel) ->Unit,
    navController: NavController
) {
    var range by remember { mutableStateOf(0f..200000000f) }
    var minValue by remember { mutableStateOf(range.start.toInt()) }
    var maxValue by remember { mutableStateOf(range.endInclusive.toInt()) }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var kategori by remember { mutableStateOf("") }
    val kategoriPemasukanList = listOf("Gaji", "Investasi", "Bisnis", "Hadiah")
    val kategoriPengeluaranList = listOf("Transportasi", "Belanja", "Pendidikan", "Hiburan")



    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0D0F13),
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
                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))

            Text("Filter", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(30.dp))

            Row {
                // Start Date Input
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {}, // Read-only
                    label = { Text("Start Date") },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select start date")
                        }
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // End Date Input
                OutlinedTextField(
                    value = endDate,
                    onValueChange = {}, // Read-only
                    label = { Text("End Date") },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select end date")
                        }
                    }
                )
            }


            // Show Start Date Picker if `showStartDatePicker` is true
            if (showStartDatePicker) {
                DatePickerDialogExample(onDatePicked = { selectedDate ->
                    startDate = selectedDate
                    showStartDatePicker = false
                })
            }

            if (showEndDatePicker) {
                DatePickerDialogExample(onDatePicked = { selectedDate ->
                    endDate = selectedDate
                    showEndDatePicker = false
                })
            }




            Spacer(Modifier.height(40.dp))

            Text("Nominal", color = Color.White)

            // RangeSlider to adjust min/max values
            RangeSlider(
                value = range,
                onValueChange = {
                    range = it
                    minValue = it.start.toInt()  // Update min value
                    maxValue = it.endInclusive.toInt()  // Update max value
                },
                valueRange = 0f..20000000f,
                steps = 200,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFF2D6CE9),
                    activeTickColor = Color(0xFF2D6CE9),
                )
            )

            // Manual input fields for min and max values with OutlinedTextField
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(Modifier.weight(1f)) {
                    Text("Min", color = Color.White) // Label outside the TextField
                    CurrencyInputField(
                        label = "Min",
                        value = minValue,
                        onValueChange = { minValue = it },
                        modifier = Modifier
                            .fillMaxWidth() // Make sure the TextField takes full width

                    )
                }


                // Spacer to add gap between TextFields
                Spacer(Modifier.width(16.dp)) // Adjust the gap size as needed

                // Max Value Label and TextField
                Column(Modifier.weight(1f)) {
                    Text("Max", color = Color.White) // Label outside the TextField
                    CurrencyInputField(
                        label = "Max",
                        value = maxValue,
                        onValueChange = { maxValue = it },
                        modifier = Modifier
                            .fillMaxWidth() // Make sure the TextField takes full width
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column {
                Text("Kategori", color = Color.White)
                Spacer(Modifier.height(10.dp))
                DropdownMenu(
                    containerColor = night,
                    label = "Kategori",
                    options = if (isPemasukan) {
                        kategoriPemasukanList
                    } else {
                        kategoriPengeluaranList
                    },

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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D6CE9),
                    contentColor = Color.White
                )
            ) {
                Text("Apply Filter")
            }



            Spacer(Modifier.height(24.dp))
        }
    }


}



@Composable
fun DatePickerDialogExample(onDatePicked: (String) -> Unit) {
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
        )
    }

    LaunchedEffect(datePickerDialog) {
        datePickerDialog.show()
    }
}










