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
import com.example.snapcash.ui.component.DropdownMenu

@Composable
fun AddBiayaDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (String, String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                var namabiaya by remember { mutableStateOf("") }
                var jumlahbiaya by remember { mutableStateOf("") }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tambahan Biaya", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = namabiaya, onValueChange = { namabiaya = it },
                        label = { Text("Nama Biaya") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = jumlahbiaya, onValueChange = { jumlahbiaya = it },
                            label = { Text("Jumlah") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            onAddItem(namabiaya, jumlahbiaya)
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

