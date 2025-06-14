package com.example.snapcash.ui.component

import androidx.benchmark.perfetto.ExperimentalPerfettoCaptureApi
import androidx.benchmark.perfetto.PerfettoConfig.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
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
import com.example.snapcash.data.SessionManager
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults

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
        value = formatCurrency(value),
        onValueChange = {
            rawInput = it
            val onlyDigits = it.replace("[^\\d]".toRegex(), "")
            onlyDigits.toIntOrNull()?.let { parsed ->
                onValueChange(parsed)
            }
        },
        label = { Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            if (!focusState.isFocused) {
                rawInput = value.toString()
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary
        )

    )
}

fun formatCurrency(amount: Int): String {
    val localeString = SessionManager.locale?.toString() ?: "id_ID"
    val localeParts = localeString.split("_")
    val locale = Locale(localeParts[0], localeParts[1])

    val format = NumberFormat.getCurrencyInstance(locale).apply {
        maximumFractionDigits = 0
    }
    return format.format(amount)
}