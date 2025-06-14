package com.example.snapcash.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.snapcash.ui.theme.night
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults

@Composable
fun AddBarangDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (String, String, String, Int) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                var namaProduk by remember { mutableStateOf("") }
                var kategori by remember { mutableStateOf("") }
                var jumlah by remember { mutableStateOf("") }
                var harga by remember { mutableStateOf(0) }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tambah Barang", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)

                    OutlinedTextField(
                        value = namaProduk, onValueChange = { namaProduk = it },
                        label = { Text("Nama Produk", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jumlah,
                            onValueChange = {
                                if (it.matches(Regex("^[1-9][0-9]*$")) || it.isEmpty()) {
                                    jumlah = it
                                }
                            },
                            label = { Text("Quantity", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        CurrencyInputField("Price", harga, { harga = it }, Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            onAddItem(namaProduk, kategori, jumlah, harga)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Add", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}