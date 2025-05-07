package com.example.snapcash.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.example.snapcash.data.Tambahanbiaya
import com.example.snapcash.ui.component.AddBiayaDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemasukanEntryScreen(
    navController: NavController,
    viewModel: PemasukanViewModel = hiltViewModel(),
    id: String?
) {
    // Context
    val context = LocalContext.current

    // Colors
    val primaryColor = Color(0xFF2D6CE9)

    var judul by remember { mutableStateOf("") }
    var sumber by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf(0) }
    var subTotal by remember { mutableStateOf(0) }
    var kategori by remember { mutableStateOf("") }
    var biayalist by remember { mutableStateOf(listOf<Tambahanbiaya>()) }
    var showDialogBiaya by remember { mutableStateOf(false) }
    var isUpdate by remember { mutableStateOf(false) }
    var totalIsUpdate by remember { mutableStateOf(0.0) }

    // ViewModel state
    val pemasukanData by remember { viewModel.pemasukanDataById }

    // Date and time setup
    val dateTimeFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("id", "ID"))
    val calendar = Calendar.getInstance()

    // Date and time picker dialogs
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    tanggal = dateTimeFormatter.format(calendar.time)
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

    // Load data if in update mode
    if (id != null) {
        LaunchedEffect(Unit) {
            viewModel.getPemasukanUserById(id)
        }

        LaunchedEffect(pemasukanData) {
            if (pemasukanData.size() > 0) {
                isUpdate = true
                judul = pemasukanData.get("namaPemasukan")?.asString ?: ""
                kategori = pemasukanData.get("kategori")?.asString ?: ""
                sumber = pemasukanData.get("sumber")?.asString ?: ""
                tanggal = pemasukanData.get("tanggal")?.asString ?: ""
                nominal = pemasukanData.get("total")?.asInt ?: 0
                subTotal = pemasukanData.get("subTotal")?.asInt ?: 0

                val biayaJsonArray = pemasukanData.get("tambahanBiaya")?.asJsonArray
                biayalist = biayaJsonArray?.map { item ->
                    val obj = item.asJsonObject
                    Tambahanbiaya(
                        namabiaya = obj.get("namaBiaya").asString,
                        jumlahbiaya = obj.get("jumlah").asDouble,
                    )
                } ?: emptyList()

                // Calculate total excluding additional costs
                val totalBiaya = biayalist.sumOf { it.jumlahbiaya }
                totalIsUpdate = if (nominal == 0) 0.0 else (nominal - totalBiaya)
                Log.d("biaya", "Total biaya: $totalBiaya, Total is update: $totalIsUpdate")
            }
        }
    }

    // Calculate totals
    val nominalValue = if (isUpdate) totalIsUpdate else subTotal.toDouble()
    val totalTambahan = biayalist.sumOf { it.jumlahbiaya }
    val total = nominalValue + totalTambahan

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = "CATAT",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                TabRow(
                    selectedTabIndex = 0,
                    contentColor = primaryColor,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[0]),
                            color = primaryColor
                        )
                    }
                ) {
                    Tab(
                        selected = true,
                        onClick = {},
                        text = { Text("INCOME", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = false,
                        onClick = {
                            navController.navigate("tambah/pengeluaran")
                        },
                        text = { Text("OUTCOME", color = Color.Gray) }
                    )
                }
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (isUpdate) {
                    FloatingActionButton(
                        containerColor = primaryColor,
                        onClick = {
                            viewModel.deletePemasukanById(id.toString(), navController)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Income"
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { showDialogBiaya = true },
                    containerColor = primaryColor
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Cost"
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Income", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        formatRupiah(total.toInt()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        if (judul.isNotBlank() && sumber.isNotBlank() && tanggal.isNotBlank() &&
                            (isUpdate || subTotal != 0)) {

                            val biayaArray = JsonArray().apply {
                                biayalist.forEach { biaya ->
                                    add(JsonObject().apply {
                                        addProperty("namaBiaya", biaya.namabiaya)
                                        addProperty("jumlah", biaya.jumlahbiaya)
                                    })
                                }
                            }

                            val request = JsonObject().apply {
                                addProperty("namaPemasukan", judul)
                                addProperty("sumber", sumber)
                                addProperty("tanggal", tanggal)
                                addProperty("total", total)
                                add("tambahanBiaya", biayaArray)
                                addProperty("isPengeluaran", false)
                            }

                            viewModel.addPemasukan(request, navController)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text("SUBMIT")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title field
            item {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Source field
            item {
                OutlinedTextField(
                    value = sumber,
                    onValueChange = { sumber = it },
                    label = { Text("Source") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Amount and date fields
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Amount field
                    OutlinedTextField(
                        value = if (isUpdate) {
                            totalIsUpdate.toInt().toString()
                        } else {
                            subTotal.toString()
                        },
                        onValueChange = {
                            if (isUpdate) {
                                totalIsUpdate = it.toDoubleOrNull() ?: 0.0
                            } else {
                                subTotal = it.toIntOrNull() ?: 0
                            }
                        },
                        label = { Text("Amount") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Rp") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Date field
                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = { /* Read-only */ },
                        label = { Text("Date") },
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) {
                                detectTapGestures { datePicker.show() }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = primaryColor,
                            disabledBorderColor = Color.Gray,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                        ),
                        readOnly = true,
                        enabled = false
                    )
                }
            }

            // Additional costs header
            item {
                Column {
                    Text(
                        "Additional Add",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (biayalist.isEmpty()) {
                        Text("There are no additional add", color = Color.Gray)
                    }
                }
            }

            // List of additional costs
            itemsIndexed(biayalist) { index, biaya ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "${biaya.namabiaya} - ${formatRupiah(biaya.jumlahbiaya.toInt())}",
                            fontSize = 14.sp
                        )
                    }
                    IconButton(
                        onClick = {
                            biayalist = biayalist.toMutableList().apply {
                                removeAt(index)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }

    // Dialog for adding additional costs
    AddBiayaDialog(
        showDialog = showDialogBiaya,
        onDismiss = { showDialogBiaya = false },
        onAddItem = { namabiaya, jumlahbiaya ->
            biayalist = biayalist + Tambahanbiaya(
                namabiaya,
                jumlahbiaya.toDouble()
            )
        }
    )
}
