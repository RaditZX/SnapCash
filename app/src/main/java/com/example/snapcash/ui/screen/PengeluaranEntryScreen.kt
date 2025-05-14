package com.example.snapcash.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import com.example.snapcash.ui.component.AddBarangDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.snapcash.ViewModel.PengeluaranViewModel
import com.example.snapcash.data.Barang
import com.example.snapcash.data.Tambahanbiaya
import com.example.snapcash.ui.component.AddBiayaDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.input.pointer.pointerInput
import com.example.snapcash.ui.component.DropdownMenu
import com.example.snapcash.ui.theme.night
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranEntryScreen(
    navController: NavController,
    id: String?,
    viewModel: PengeluaranViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // State untuk form input
    var judul by remember { mutableStateOf("") }
    var toko by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var total by remember { mutableStateOf(0) }
    var request by remember { mutableStateOf(JsonObject()) }
    var kategori by remember { mutableStateOf("")}
    val pengeluaranData by remember { viewModel.pengeluaranDataById }
    val isLoading by viewModel.isLoading
    var isUpdate by remember { mutableStateOf(false) }
    val kategoriList = listOf("Transportasi", "Belanja", "Pendidikan", "Hiburan")
    val dateTimeFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale("id", "ID"))
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0) // Set detik ke 0
                    tanggal = dateTimeFormatter.format(calendar.time)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )


    // State untuk menampilkan dialog
    var showDialog by remember { mutableStateOf(false) }
    var showDialogBiaya by remember { mutableStateOf(false) }
    var barangList by remember { mutableStateOf(listOf<Barang>()) }
    var biayalist by remember { mutableStateOf(listOf<Tambahanbiaya>()) }
    if (id != null) {
        LaunchedEffect(Unit) {
            viewModel.getPengluaranUserById(id.toString())


        }
        LaunchedEffect(pengeluaranData) {
            if (pengeluaranData.size() > 0) {
                isUpdate = true
                judul = pengeluaranData.get("namaPengeluaran")?.asString ?: ""
                toko = pengeluaranData.get("toko")?.asString ?: ""
                tanggal = pengeluaranData.get("tanggal")?.asString ?: ""
                kategori = pengeluaranData.get("kategori")?.asString ?: ""
                val barangJsonArray = pengeluaranData.get("barang")?.asJsonArray
                barangList = barangJsonArray?.map { item ->
                    val obj = item.asJsonObject
                    Barang(
                        nama = obj.get("namaBarang").asString,
                        jumlah = obj.get("jumlah").asInt,
                        harga = obj.get("harga").asDouble
                    )
                } ?: emptyList()

                val biayaJsonArray = pengeluaranData.get("tambahanBiaya")?.asJsonArray
                biayalist = biayaJsonArray?.map { item ->
                    val obj = item.asJsonObject
                    Tambahanbiaya(
                        namabiaya = obj.get("namaBiaya").asString,
                        jumlahbiaya = obj.get("jumlah").asDouble,
                    )
                } ?: emptyList()


            }
        }

    }

    LaunchedEffect(barangList, biayalist) {
        val totalBarang = barangList.sumOf { (it.harga.toInt()) * (it.jumlah.toInt()) }
        val totalBiaya = biayalist.sumOf { it.jumlahbiaya.toInt() }
        total = totalBarang + totalBiaya
    }


    val barangArray = JsonArray()
    barangList.forEach { barang ->
        val barangObj = JsonObject().apply {
            addProperty("namaBarang", barang.nama)
            addProperty("jumlah", barang.jumlah)
            addProperty("harga", barang.harga)
        }
        barangArray.add(barangObj)
    }

    val biayaArray = JsonArray()
    biayalist.forEach { biaya ->
        val biayaObj = JsonObject().apply {
            addProperty("namaBiaya", biaya.namabiaya)
            addProperty("jumlah", biaya.jumlahbiaya)
        }
        biayaArray.add(biayaObj)
    }

    request = JsonObject().apply {
        addProperty("namaPengeluaran", judul)
        addProperty("toko", toko)
        addProperty("tanggal", tanggal)
        addProperty("total", total)
        add("barang", barangArray)
        addProperty("kategori", kategori)
        add("tambahanBiaya", biayaArray)

    }



    if (isLoading) {
        // 👇 Show loading UI while waiting
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                Column {
                    Text(
                        text = "CATAT",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    TabRow(selectedTabIndex = 1,contentColor = Color(0xFF2D6CE9),indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[1]),
                            color = Color(0xFF2D6CE9) // 👈 your custom underline color
                        )
                    }) {
                        Tab(
                            selected = false,
                            onClick = {
                                navController.navigate("tambah/pemasukan") // Sesuaikan dengan route-mu
                            },
                            text = { Text("INCOME", color = Color.Gray) }
                        )
                        Tab(
                            selected = true,
                            onClick = { /* Stay here */ },
                            text = { Text("OUTCOME", fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            },
            floatingActionButton = {
                    if (isUpdate){
                        FloatingActionButton(containerColor = Color(0xFF2D6CE9),onClick = {
                            viewModel.deletePengeluaranById(id.toString(), navController)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Pengeluaran"
                            )
                        }
                    }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Outcome", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            formatRupiah(total),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            if (isUpdate) {
                                viewModel.updatePengeluaranUserById(
                                    id.toString(),
                                    request,
                                    navController
                                )
                            } else {
                                viewModel.addPengeluaran(request, navController)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color(0xFF2D6CE9),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.Gray,
                        ),
                    ) {
                        Text("SUBMIT")
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Form
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = judul,
                            onValueChange = { judul = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray,
                                focusedBorderColor = Color.Blue
                            )
                        )

                        OutlinedTextField(
                            value = toko,
                            onValueChange = { toko = it },
                            label = { Text("Store") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray,
                                focusedBorderColor = Color.Blue
                            )
                        )

                        OutlinedTextField(
                            value = tanggal,
                            onValueChange = { /* Read-only */ },
                            label = { Text("Date") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures { datePicker.show() }
                                },
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { datePicker.show() }) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Pilih Tanggal"
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray,
                                focusedBorderColor = Color.Blue,
                                disabledBorderColor = Color.Gray,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                            ),
                            readOnly = true,
                            enabled = false
                        )

                        DropdownMenu(
                            containerColor = night,
                            label = "Kategori",
                            options = kategoriList,
                            selectedOption = kategori,
                            onOptionSelected = { kategori = it }
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "List Item",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                showDialogBiaya = false
                                showDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Tambah Barang",
                                tint = Color(0xFF2D6CE9)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (barangList.isEmpty()) {
                        Text("There are no items", color = Color.Gray)
                    }
                }

                // Daftar Barang
                itemsIndexed(barangList) { index, barang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                barang.nama,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                "Quantity: ${barang.jumlah} | Price: ${formatRupiah(barang.harga.toInt())}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(onClick = {
                            barangList = barangList.toMutableList().apply { removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }

                // Header Biaya
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Additional Cost",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                showDialog = false
                                showDialogBiaya = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Biaya",
                                tint = Color(0xFF2D6CE9)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (biayalist.isEmpty()) {
                        Text("There are no additional costs", color = Color.Gray)
                    }
                }

                // Daftar Biaya
                itemsIndexed(biayalist) { index, biaya ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("${biaya.namabiaya} - ${formatRupiah(biaya.jumlahbiaya.toInt())}", fontSize = 14.sp)
                        }
                        IconButton(onClick = {
                            biayalist = biayalist.toMutableList().apply { removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // Panggil Dialog
    AddBarangDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onAddItem = { nama, kategori, jumlah, harga ->
            barangList = barangList + Barang(nama, jumlah.toInt(), harga.toDouble())
        }
    )

    AddBiayaDialog(
        showDialog = showDialogBiaya,
        onDismiss = { showDialogBiaya = false },
        onAddItem = { namabiaya, jumlahbiaya ->
            biayalist = biayalist + Tambahanbiaya(namabiaya, jumlahbiaya.toDouble())
        }
    )
}

fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
