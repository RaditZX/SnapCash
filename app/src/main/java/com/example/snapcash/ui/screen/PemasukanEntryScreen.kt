package com.example.snapcash.ui.screen

import com.example.snapcash.ui.component.DropdownMenu
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.snapcash.ViewModel.PemasukanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemasukanEntryScreen(navController: NavController, id : String?, viewModel: PemasukanViewModel = hiltViewModel()) {
    var judul by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf(0) }
    var kategori by remember { mutableStateOf("") }
    var sumber by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("Pilih Tanggal") }
    val pemasukanData by remember { viewModel.pemasukanDataById }
    var isUpdate by remember {mutableStateOf(false)}
    if (id != null) {
        LaunchedEffect(Unit) {
            viewModel.getPemasukanUserById(id.toString())
        }

        LaunchedEffect(pemasukanData) {
            if (pemasukanData.size() > 0) {
                isUpdate = true
                judul = pemasukanData.get("namaPemasukan")?.asString ?: ""
                kategori = pemasukanData.get("kategori")?.asString ?: ""
                sumber = pemasukanData.get("sumber")?.asString ?: ""
                tanggal = pemasukanData.get("tanggal")?.asString ?: ""
                nominal = pemasukanData.get("total")?.asInt ?: 0
            }


        }


    }

    // Data kategori & sub kategori
    val kategoriList = listOf("Gaji", "Investasi", "Bisnis", "Hadiah")

    // Date Picker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
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
                title = { Text("INCOME") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (isUpdate) {
                    FloatingActionButton(onClick = {
                        viewModel.deletePemasukanById(id.toString(), navController)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Pengeluaran"
                        )
                    }
                }
            }
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
                        value = nominal.toString(),
                        onValueChange = { nominal = it.toInt() },
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
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Submit
            Button(
                onClick = {},
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
