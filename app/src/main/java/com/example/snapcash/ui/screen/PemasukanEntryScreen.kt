package com.example.snapcash.ui.screen

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import com.example.snapcash.ui.component.AddBiayaDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemasukanEntryScreen(
    navController: NavController,
    viewModel: PemasukanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var judul by remember { mutableStateOf("") }
    var sumber by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var biayalist by remember { mutableStateOf(listOf<Tambahanbiaya>()) }

    // Toggle form tambahan biaya
    var showDialogBiaya by remember { mutableStateOf(false) }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            tanggal = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                TabRow(selectedTabIndex = 0) {
                    Tab(
                        selected = true,
                        onClick = { /* Do nothing, stay on this screen */ },
                        text = { Text("INCOME", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = false,
                        onClick = {
                            navController.navigate("tambah/pengeluaran") // Sesuaikan dengan route-mu
                        },
                        text = { Text("OUTCOME", color = Color.Gray) }
                    )
                }
            }
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { showDialogBiaya = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Biaya")
            }
        },
        bottomBar = {
            val nominalValue = nominal.toDoubleOrNull() ?: 0.0
            val totalTambahan = biayalist.sumOf { it.jumlahbiaya }
            val total = nominalValue + totalTambahan

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
                    Text("Total Pemasukan", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        formatRupiah(total.toInt()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        if (judul.isNotBlank() && sumber.isNotBlank() && tanggal.isNotBlank() && nominal.isNotBlank()) {
                            val biayaArray = JsonArray()
                            biayalist.forEach {
                                val biayaObj = JsonObject().apply {
                                    addProperty("namaBiaya", it.namabiaya)
                                    addProperty("jumlah", it.jumlahbiaya)
                                }
                                biayaArray.add(biayaObj)
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
                    shape = RoundedCornerShape(12.dp)
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
            item {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = sumber,
                    onValueChange = { sumber = it },
                    label = { Text("Sumber") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nominal,
                        onValueChange = { nominal = it },
                        label = { Text("Nominal") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Rp") }
                    )
                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = {},
                        label = { Text("Tanggal") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Header Tambahan Biaya
            item {
                Column {
                    Text("Tambahan Biaya", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (biayalist.isEmpty()) {
                        Text("Belum ada Tambahan Biaya", color = Color.Gray)
                    }
                }
            }

            // List Biaya Tambahan
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
                        Text("${biaya.namabiaya} - ${formatRupiah(biaya.jumlahbiaya.toInt())}", fontSize = 14.sp)
                    }
                    IconButton(onClick = {
                        biayalist = biayalist.toMutableList().apply { removeAt(index) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                    }
                }
            }
        }
    }

    AddBiayaDialog(
        showDialog = showDialogBiaya,
        onDismiss = { showDialogBiaya = false },
        onAddItem = { namabiaya, jumlahbiaya ->
            biayalist = biayalist + Tambahanbiaya(namabiaya, jumlahbiaya.toDouble())
        }
    )
}

