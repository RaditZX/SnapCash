package com.example.snapcash.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AddBarangDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (String, String, String, String, String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                var namaProduk by remember { mutableStateOf("") }
                var kategori by remember { mutableStateOf("") }
                var subkategori by remember { mutableStateOf("") }
                var jumlah by remember { mutableStateOf("") }
                var harga by remember { mutableStateOf("") }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tambah Barang", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = namaProduk, onValueChange = { namaProduk = it },
                        label = { Text("Nama Produk") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = kategori, onValueChange = { kategori = it },
                            label = { Text("Kategori") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = subkategori, onValueChange = { subkategori = it },
                            label = { Text("Subkategori") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jumlah, onValueChange = { jumlah = it },
                            label = { Text("Jumlah") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = harga, onValueChange = { harga = it },
                            label = { Text("Harga") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            onAddItem(namaProduk, kategori, subkategori, jumlah, harga)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Blue)
                    ) {
                        Text("Tambah", color = Color.White)
                    }
                }
            }
        }
    }
}
