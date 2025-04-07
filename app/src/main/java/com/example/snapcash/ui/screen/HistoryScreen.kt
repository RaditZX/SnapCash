package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val title: String,
    val category: String,
    val amount: Int,
    val date: String // Format: "dd/MM/yyyy"
)

@Composable
fun HistoryScreen(navController: NavController) {
    var selectedType by remember { mutableStateOf("Money Outcome") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isDescending by remember { mutableStateOf(true) }

    val incomeTransactions = listOf(
        Transaction("SALARY", "WORK", 980000, "20/08/2025"),
        Transaction("FREELANCE PROJECT", "SIDE JOB", 150000, "18/08/2025"),
        Transaction("GIFT FROM FRIEND", "GIFT", 100000, "15/08/2025")
    )

    val outcomeTransactions = listOf(
        Transaction("BAJU UNIQLO", "FASHION", -98000, "19/08/2025"),
        Transaction("RESTAURANT BBQ", "FOOD", -98000, "19/08/2025"),
        Transaction("ELECTRIC CAR", "CAR", -98000, "17/08/2025"),
        Transaction("DRINKS AND DISCO", "PARTY", -98000, "16/08/2025"),
        Transaction("CEDINT RENT", "APARTMENT", -98000, "14/08/2025")
    )

    var currentTransactions by remember(selectedType) {
        mutableStateOf(
            if (selectedType == "Money Income") incomeTransactions else outcomeTransactions
        )
    }

    // Sort otomatis berdasarkan tanggal terbaru setiap kali selectedType berubah
    LaunchedEffect(selectedType) {
        currentTransactions = if (selectedType == "Money Income") {
            incomeTransactions.sortedByDescending { parseDate(it.date) }
        } else {
            outcomeTransactions.sortedByDescending { parseDate(it.date) }
        }
        isDescending = true
    }

    val displayedTransactions = if (isDescending) {
        currentTransactions
    } else {
        currentTransactions.reversed()
    }

    val totalAmount = displayedTransactions.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "HISTORY",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color.LightGray, RoundedCornerShape(50))
                .clickable { isDropdownExpanded = true },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedType)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                DropdownMenuItem(text = { Text("Money Income") }, onClick = {
                    selectedType = "Money Income"
                    isDropdownExpanded = false
                })
                DropdownMenuItem(text = { Text("Money Outcome") }, onClick = {
                    selectedType = "Money Outcome"
                    isDropdownExpanded = false
                })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("TOTAL", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                formatCurrencyWithSign(totalAmount),
                color = if (selectedType == "Money Income") Color(0xFF4CAF50) else Color.Red,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (isDescending) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                contentDescription = "Sort Icon",
                tint = if (isDescending) Color.Red else Color(0xFF4CAF50),
                modifier = Modifier.clickable { isDescending = !isDescending }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(displayedTransactions) { transaction ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(transaction.title, fontWeight = FontWeight.Bold)
                    Text(transaction.category.uppercase(), fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                        Text(
                            formatCurrencyWithSign(transaction.amount),
                            color = if (selectedType == "Money Income") Color(0xFF4CAF50) else Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// Parse tanggal dari string ke Date untuk sorting
fun parseDate(dateString: String): Date {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.parse(dateString) ?: Date(0)
}

// Format uang dengan tanda + atau -
fun formatCurrencyWithSign(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    val absoluteAmount = format.format(kotlin.math.abs(amount))
    return if (amount >= 0) "+$absoluteAmount" else "-$absoluteAmount"
}
