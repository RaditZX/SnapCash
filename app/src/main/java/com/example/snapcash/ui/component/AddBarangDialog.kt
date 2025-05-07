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
import com.example.snapcash.ui.theme.night

@Composable
fun AddBarangDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (String, String, String, String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(night, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                var namaProduk by remember { mutableStateOf("") }
                var kategori by remember { mutableStateOf("") }
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
                            value = jumlah, onValueChange = { jumlah = it },
                            label = { Text("Quantity") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = harga, onValueChange = { harga = it },
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
