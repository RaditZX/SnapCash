package com.example.snapcash.ui.component

import androidx.benchmark.perfetto.ExperimentalPerfettoCaptureApi
import androidx.benchmark.perfetto.PerfettoConfig.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalPerfettoCaptureApi::class)
@Composable
fun CurrencyInputField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var rawInput by remember { mutableStateOf(value.toString()) }
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value =  formatCurrency(value),
        onValueChange = {
            rawInput = it
            val onlyDigits = it.replace("[^\\d]".toRegex(), "")
            onlyDigits.toIntOrNull()?.let { parsed ->
                onValueChange(parsed)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            if (!focusState.isFocused) {
                rawInput = value.toString()
            }
        }
    )
}


fun formatCurrency(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
        currency = Currency.getInstance("IDR")
    }
    return format.format(amount)
}
