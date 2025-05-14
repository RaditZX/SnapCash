package com.example.snapcash.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.example.snapcash.ViewModel.PengeluaranViewModel
import com.example.snapcash.data.FilterModel
import com.example.snapcash.data.SessionManager
import com.example.snapcash.ui.component.SearchWithFilterBar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Transaction(
    val title: String,
    val category: String,
    val amount: Int,
    val date: String,
    val id: String,
    val isPengeluaran: Boolean
)

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: PengeluaranViewModel = hiltViewModel(),
    viewModel2: PemasukanViewModel = hiltViewModel(),
    onFilterClick: () -> Unit,
    isPemasukan: (Boolean) -> Unit,
    filterData : FilterModel
) {
    var selectedType by remember { mutableStateOf("Money Outcome") }
    var isDescending by remember { mutableStateOf(true) }
    val pengeluaranData by remember { viewModel.pengeluaranData }
    val pemasukanData by remember { viewModel2.pemasukanData }
    val isLoading by viewModel.isLoading
    val isLoadingPemasukan by viewModel2.isLoading
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getPengeluaranUser(filterData)
        viewModel2.getPemasukanUser(filterData)
    }

    Log.d("filter", filterData.toString())


    if (isLoading || isLoadingPemasukan) {
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
                        item.get("tanggal")?.asString ?: "",
                        item.get("id")?.asString ?: "",
                        item.get("isPengeluaran")?.asBoolean ?: false
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
                        item.get("tanggal")?.asString ?: "",
                        item.get("id")?.asString ?: "",
                        item.get("isPengeluaran")?.asBoolean ?: false
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
            Text(
                text = "HISTORY",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            val selectedTabIndex = if (selectedType == "Money Income") 0 else 1

            TabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(3.dp),
                        color = Color(0xFF3F51B5)
                    )
                },
                containerColor = Color.Transparent,
                divider = {
                    Divider(thickness = 1.dp, color = Color.LightGray)
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedType = "Money Income"
                        isPemasukan(true) // kirimkan boolean ke atas
                    },
                    text = {
                        Text(
                            "INCOME",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabIndex == 0) Color(0xFF3F51B5) else Color.Gray
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedType = "Money Outcome"
                        isPemasukan(false) },
                    text = {
                        Text(
                            "OUTCOME",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTabIndex == 1) Color(0xFF3F51B5) else Color.Gray
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            SearchWithFilterBar(
                modifier = Modifier,
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = {onFilterClick()}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TOTAL", fontWeight = FontWeight.Bold)
                    Text(
                        formatCurrencyWithSign(totalAmount, selectedType == "Money Outcome"),
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
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable {
                                if (transaction.isPengeluaran) {
                                    navController.navigate("update/pengeluaran/${transaction.id}")
                                } else {
                                    navController.navigate("update/pemasukan/${transaction.id}")
                                }
                            }
                    ) {
                        Text(transaction.title, fontWeight = FontWeight.Bold)
                        Text(transaction.category.uppercase(), fontSize = 12.sp, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                            Text(
                                formatCurrencyWithSign(transaction.amount, transaction.isPengeluaran),
                                color = if (transaction.isPengeluaran) Color.Red else Color(0xFF4CAF50),
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



fun formatCurrencyWithSign(amount: Int, isPengeluaran: Boolean): String {
    val localeString = SessionManager.locale?.toString() ?: "id_ID"
    val localeParts = localeString.split("_")
    val locale = Locale(localeParts[0], localeParts[1])

    val format = NumberFormat.getCurrencyInstance(locale)
    format.maximumFractionDigits = 0
    val absoluteAmount = format.format(kotlin.math.abs(amount))
    return if (isPengeluaran) "-$absoluteAmount" else "+$absoluteAmount"
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


