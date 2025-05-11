package com.example.snapcash.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.snapcash.ui.theme.night

@Composable
fun AddBiayaDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (String, Int) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .background(night, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                var namabiaya by remember { mutableStateOf("") }
                var jumlahbiaya by remember { mutableStateOf(0) }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tambahan Biaya", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = namabiaya, onValueChange = { namabiaya = it },
                        label = { Text("Cost Title") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )


                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CurrencyInputField(
                            "Cost",
                            jumlahbiaya,
                            onValueChange = { jumlahbiaya = it},
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Button(
                        onClick = {
                            onAddItem(namabiaya, jumlahbiaya)
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

