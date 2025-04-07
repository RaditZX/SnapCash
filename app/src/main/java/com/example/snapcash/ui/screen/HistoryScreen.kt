package com.example.snapcash.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.AuthViewModel
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.example.snapcash.ViewModel.PengeluaranViewModel
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val title: String,
    val category: String,
    val amount: Int,
    val date: String
)

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: PengeluaranViewModel = hiltViewModel(),
    viewModel2: PemasukanViewModel = hiltViewModel()
) {
    var selectedType by remember { mutableStateOf("Money Outcome") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isDescending by remember { mutableStateOf(true) }

    val pengeluaranData by remember { viewModel.pengeluaranData }
    val pemasukanData by remember { viewModel2.pemasukanData }
    val isLoading by viewModel.isLoading
    val isLoadingPemasukan by viewModel2.isLoading
    LaunchedEffect(Unit) {
        viewModel.getPengeluaranUser()
        viewModel2.getPemasukanUser()
    }



    if (isLoading || isLoadingPemasukan) {
        // 👇 Show loading UI while waiting
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {

        val incomeTransactions by remember(pengeluaranData) {
            derivedStateOf {
                pemasukanData.map { item ->
                    Log.d("pemasukan", item.toString())
                    Transaction(
                        item.get("namaPemasukan")?.asString ?: "",
                        item.get("sumber")?.asString ?: "",
                        item.get("total")?.asInt ?: 0,
                        item.get("tanggal")?.asString ?: ""
                    )
                }
            }
        }

        val outcomeTransactions by remember(pengeluaranData) {
            derivedStateOf {
                pengeluaranData.map { item ->
                    Transaction(
                        item.get("namaPengeluaran")?.asString ?: "",
                        item.get("toko")?.asString ?: "",
                        item.get("total")?.asInt ?: 0,
                        item.get("tanggal")?.asString ?: ""
                    )
                }
            }
        }

        var currentTransactions by remember(selectedType) {
            mutableStateOf(
                if (selectedType == "Money Income") incomeTransactions else outcomeTransactions
            )
        }

        LaunchedEffect(selectedType) {
            currentTransactions = if (selectedType == "Money Income") {
                incomeTransactions.sortedByDescending {
                    parseIndoDateToDate(it.date) ?: Date(0)
                }
            } else {
                outcomeTransactions.sortedByDescending {
                    parseIndoDateToDate(it.date) ?: Date(0)
                }
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
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(50))
                    .clickable { isDropdownExpanded = true }
            ) {
                // Center text
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(selectedType, color = Color.Black)
                }

                // Icon at the end (right-aligned)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        tint = Color.Black,
                        contentDescription = "Dropdown"
                    )
                }



                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth() // <- Important
                ) {
                    listOf("Money Income", "Money Outcome").forEach { type ->
                        DropdownMenuItem(
                            text = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray, RoundedCornerShape(50))
                                        .padding(vertical = 8.dp, horizontal = 16.dp),
                                    contentAlignment = Alignment.Center

                                ) {
                                    Text(
                                        text = type,
                                        color = Color.Black,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            },
                            onClick = {
                                selectedType = type
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TOTAL", fontWeight = FontWeight.Bold)
                    Text(
                        formatCurrencyWithSign(totalAmount),
                        color = if (selectedType == "Money Income") Color(0xFF4CAF50) else Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                }
                Icon(
                    imageVector = if (isDescending) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
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
}


fun formatCurrencyWithSign(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    val absoluteAmount = format.format(kotlin.math.abs(amount))
    return if (amount >= 0) "+$absoluteAmount" else "-$absoluteAmount"
}

fun translateIndonesianDate(dateStr: String): String {
    val monthMap = mapOf(
        "Januari" to "January",
        "Februari" to "February",
        "Maret" to "March",
        "April" to "April",
        "Mei" to "May",
        "Juni" to "June",
        "Juli" to "July",
        "Agustus" to "August",
        "September" to "September",
        "Oktober" to "October",
        "November" to "November",
        "Desember" to "December"
    )

    var result = dateStr
    monthMap.forEach { (indo, eng) ->
        result = result.replace(indo, eng)
    }
    return result.replace("WIB", "GMT+7") // replace WIB with usable timezone
}

fun parseIndoDateToDate(dateString: String): Date? {
    return try {
        val indoLocale = Locale("id", "ID")
        val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss z", indoLocale)
        formatter.parse(dateString)
    } catch (e: Exception) {
        Log.e("ParseDate", "Error parsing date: $dateString", e)
        null
    }
}


