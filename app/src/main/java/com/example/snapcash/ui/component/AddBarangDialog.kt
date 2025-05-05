package com.example.snapcash.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.KategoriViewModel
import com.example.snapcash.ui.theme.night

@Composable
fun AddBarangDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (String, String, String, String) -> Unit,
    navController: NavController
) {
    val viewModel: KategoriViewModel = hiltViewModel()
    var namaProduk by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var jumlah by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }

    // Ambil daftar kategori saat dialog dibuka
    LaunchedEffect(showDialog) {
        if (showDialog) {
            viewModel.getKategori()
        }
    }

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(night, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tambah Barang", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = namaProduk,
                        onValueChange = { namaProduk = it },
                        label = { Text("Nama Produk") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        var expanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = kategori,
                                onValueChange = { kategori = it },
                                label = { Text("Kategori") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Pilih Kategori"
                                        )
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                viewModel.kategoriList.value
                                    .filter { it.get("isPengeluaran")?.asBoolean == true } // Hanya kategori pengeluaran
                                    .forEach { kategoriItem ->
                                        val kategoriName = kategoriItem.get("namaKategori")?.asString ?: ""
                                        DropdownMenuItem(
                                            text = { Text(kategoriName) },
                                            onClick = {
                                                kategori = kategoriName
                                                expanded = false
                                            }
                                        )
                                    }
                                DropdownMenuItem(
                                    text = { Text("Tambah Kategori Baru") },
                                    onClick = {
                                        expanded = false
                                        navController.navigate("kategori") {
                                            // Pastikan layar sebelumnya tetap ada di stack
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jumlah,
                            onValueChange = { jumlah = it },
                            label = { Text("Quantity") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = harga,
                            onValueChange = { harga = it },
                            label = { Text("Price") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            onAddItem(namaProduk, kategori, jumlah, harga)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color(0xFF2D6CE9))
                    ) {
                        Text("Add", color = Color.White)
                    }
                }
            }
        }
    }
}