package com.example.snapcash.ui.screen

import com.example.snapcash.ui.component.DropdownMenu
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.google.gson.JsonObject

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
    var kategori by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }

    val kategoriList = listOf("Gaji", "Investasi", "Bisnis", "Hadiah")

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
            TopAppBar(
                title = { Text("Tambah Pemasukan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Judul", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Kategori", style = MaterialTheme.typography.labelMedium)
            DropdownMenu(
                label = "",
                options = kategoriList,
                selectedOption = kategori,
                onOptionSelected = { kategori = it }
            )

            Text("Sumber", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = sumber,
                onValueChange = { sumber = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Nominal", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = nominal,
                        onValueChange = { nominal = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rp") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Tanggal", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        readOnly = true
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (judul.isNotBlank() && sumber.isNotBlank() && tanggal.isNotBlank() && nominal.isNotBlank()) {
                        val request = JsonObject().apply {
                            addProperty("namaPemasukan", judul)
                            addProperty("sumber", kategori)
                            addProperty("tanggal", tanggal)
                            addProperty("total", nominal.toIntOrNull() ?: 0)
                            addProperty("tambahanBiaya", sumber)
                            addProperty("isPengeluaran", false)
                        }

                        viewModel.addPemasukan(request, navController)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SUBMIT")
            }
        }
    }
}

