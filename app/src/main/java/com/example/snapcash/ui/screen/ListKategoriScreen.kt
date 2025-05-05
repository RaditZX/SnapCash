package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.KategoriViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.google.gson.JsonObject
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListKategoriScreen(
    navController: NavController,
    viewModel: KategoriViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var kategoriName by remember { mutableStateOf("") }
    var isPengeluaran by remember { mutableStateOf(true) }
    var selectedJenisKategori by remember { mutableStateOf("Pengeluaran") } // Untuk Dropdown
    var selectedKategoriId by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) } // Untuk memicu pembaruan daftar

    // Memuat daftar kategori saat layar pertama kali dibuka atau setelah refresh
    LaunchedEffect(refreshTrigger) {
        viewModel.getKategori()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Kategori") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                    selectedKategoriId = null
                    kategoriName = ""
                    isPengeluaran = true
                    selectedJenisKategori = "Pengeluaran" // Default ke Pengeluaran
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kategori")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewModel.isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (viewModel.kategoriList.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada kategori. Tambahkan kategori baru.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(viewModel.kategoriList.value) { kategori ->
                        KategoriItem(
                            kategori = kategori,
                            onEditClick = {
                                selectedKategoriId = kategori.get("id")?.asString ?: ""
                                kategoriName = kategori.get("namaKategori")?.asString ?: ""
                                isPengeluaran = kategori.get("isPengeluaran")?.asBoolean ?: true
                                selectedJenisKategori = if (isPengeluaran) "Pengeluaran" else "Pemasukan"
                                showDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteKategori(
                                    kategori.get("id")?.asString ?: "",
                                    navController
                                )
                                refreshTrigger++ // Memicu pembaruan daftar setelah hapus
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (selectedKategoriId == null) "Tambah Kategori" else "Edit Kategori") },
            text = {
                Column {
                    TextField(
                        value = kategoriName,
                        onValueChange = { kategoriName = it },
                        label = { Text("Nama Kategori") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedJenisKategori,
                            onValueChange = { selectedJenisKategori = it },
                            label = { Text("Jenis Kategori") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Pilih Jenis Kategori"
                                    )
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(
                                text = { Text("Pengeluaran") },
                                onClick = {
                                    selectedJenisKategori = "Pengeluaran"
                                    isPengeluaran = true
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pemasukan") },
                                onClick = {
                                    selectedJenisKategori = "Pemasukan"
                                    isPengeluaran = false
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val kategoriData = JsonObject().apply {
                            addProperty("namaKategori", kategoriName)
                            addProperty("isPengeluaran", isPengeluaran)
                        }
                        if (selectedKategoriId == null) {
                            viewModel.addKategori(kategoriData, navController)
                            // Kembali ke layar sebelumnya setelah menambah kategori
                            navController.popBackStack()
                        } else {
                            viewModel.updateKategori(selectedKategoriId!!, kategoriData, navController)
                            refreshTrigger++ // Memicu pembaruan daftar setelah edit
                        }
                        showDialog = false
                    },
                    enabled = kategoriName.isNotBlank()
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun KategoriItem(
    kategori: JsonObject,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${kategori.get("namaKategori")?.asString ?: "Tanpa Nama"} (${if (kategori.get("isPengeluaran")?.asBoolean == true) "Pengeluaran" else "Pemasukan"})",
                style = MaterialTheme.typography.bodySmall
            )
            Row {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Kategori",
                    modifier = Modifier
                        .clickable { onEditClick() }
                        .padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Kategori",
                    modifier = Modifier.clickable { onDeleteClick() },
                    tint = Color.Red
                )
            }
        }
    }
}