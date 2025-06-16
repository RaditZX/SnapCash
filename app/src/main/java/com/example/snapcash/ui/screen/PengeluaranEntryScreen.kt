package com.example.snapcash.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.CategoryViewModel
import com.example.snapcash.ViewModel.PengeluaranViewModel
import com.example.snapcash.data.Barang
import com.example.snapcash.data.Tambahanbiaya
import com.example.snapcash.ui.component.AddBarangDialog
import com.example.snapcash.ui.component.AddBiayaDialog
import com.example.snapcash.ui.component.DropdownMenu
import com.example.snapcash.ui.component.ModernAlertDialog
import com.example.snapcash.ui.theme.night
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PengeluaranEntryScreen(
    navController: NavController,
    id: String?,
    viewModel: PengeluaranViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    preview: Boolean
) {
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }

    var judul by remember { mutableStateOf("") }
    var toko by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var total by remember { mutableStateOf(0) }
    var request by remember { mutableStateOf(JsonObject()) }
    var kategori by remember { mutableStateOf("") }
    val pengeluaranData by remember { viewModel.pengeluaranDataById }
    val isLoading by viewModel.isLoading
    var isUpdate by remember { mutableStateOf(false) }
    val categories by categoryViewModel.categories
    val kategoriList = listOf("Transportasi", "Belanja", "Pendidikan", "Hiburan")
    val allCategories = remember(categories) {
        (kategoriList + categories.filter { it.isPengeluaran }.map { it.nama }).distinct()
    }
    val categoriesWithAddOption = remember(allCategories) {
        allCategories + "Tambah Kategori"
    }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogBiaya by remember { mutableStateOf(false) }
    var barangList by remember { mutableStateOf(listOf<Barang>()) }
    var biayalist by remember { mutableStateOf(listOf<Tambahanbiaya>()) }
    val showDialogMessage = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        categoryViewModel.getAllCategories()
    }

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
                    calendar.set(Calendar.SECOND, 0)
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
                        jumlahbiaya = obj.get("jumlah").asDouble
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        }
    } else {
        Scaffold(
            topBar = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (preview) {
                            IconButton(onClick = { showCancelDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                        Text(
                            text = "CATAT",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    TabRow(
                        selectedTabIndex = 1,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[1]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Tab(
                            selected = false,
                            onClick = { navController.navigate("tambah/pemasukan") },
                            text = { Text("INCOME", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        )
                        Tab(
                            selected = true,
                            onClick = {},
                            text = { Text("OUTCOME", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (isUpdate) {
                    FloatingActionButton(
                        onClick = { viewModel.deletePengeluaranById(id.toString(), navController,onResult = { success, message ->
                            dialogMessage.value = message  // Update the popup message
                            showDialogMessage.value = true  // Show the popup
                            isSuccess.value = success
                        }) }

                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Pengeluaran",
                            tint = MaterialTheme.colorScheme.onPrimary
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
                        Text("Total Outcome", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                        Text(
                            formatRupiah(total),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Button(
                        onClick = {
                            if (isUpdate) {
                                viewModel.updatePengeluaranUserById(id.toString(), request, navController,onResult = { success, message ->
                                    dialogMessage.value = message  // Update the popup message
                                    showDialogMessage.value = true  // Show the popup
                                    isSuccess.value = success
                                })
                            } else {
                                viewModel.addPengeluaran(request, navController,onResult = { success, message ->
                                    dialogMessage.value = message  // Update the popup message
                                    showDialogMessage.value = true  // Show the popup
                                    isSuccess.value = success
                                })
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("SUBMIT", color = MaterialTheme.colorScheme.onPrimary)
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
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = judul,
                            onValueChange = { judul = it },
                            label = { Text("Title", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        OutlinedTextField(
                            value = toko,
                            onValueChange = { toko = it },
                            label = { Text("Store", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        OutlinedTextField(
                            value = tanggal,
                            onValueChange = {},
                            label = { Text("Date", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) { detectTapGestures { datePicker.show() } },
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(onClick = { datePicker.show() }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                            ),
                            readOnly = true,
                            enabled = false
                        )
                        DropdownMenu(
                            label = "Kategori",
                            options = categoriesWithAddOption,
                            selectedOption = kategori,
                            onOptionSelected = { selected ->
                                if (selected == "Tambah Kategori") {
                                    navController.navigate("kategori")
                                } else {
                                    kategori = selected
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.surface,
                            customOptionContent = { option ->
                                @Composable {
                                    if (option == "Tambah Kategori") {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text(option, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Tambah Kategori",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        Text(option, modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(
                            onClick = {
                                showDialogBiaya = false
                                showDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Barang",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (barangList.isEmpty()) {
                        Text("There are no items", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
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
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "Quantity: ${barang.jumlah} | Price: ${formatRupiah(barang.harga.toInt())}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            barangList = barangList.toMutableList().apply { removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Additional Cost",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
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
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (biayalist.isEmpty()) {
                        Text("There are no additional costs", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
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
                            Text("${biaya.namabiaya} - ${formatRupiah(biaya.jumlahbiaya.toInt())}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                        }
                        IconButton(onClick = {
                            biayalist = biayalist.toMutableList().apply { removeAt(index) }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
        // 🔄 Overlay Loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // 📦 Overlay Dialog
        if (showDialogMessage.value) {
            ModernAlertDialog(
                showDialogMessage,
                "Income",
                dialogMessage.value,
                if (isSuccess.value) "history" else null,
                navController
            )

        }
    }

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

    if (showCancelDialog && preview) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Konfirmasi", color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("Apakah Anda yakin ingin kembali?", color = MaterialTheme.colorScheme.onBackground) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePengeluaranById(id.toString(), navController,onResult = { success, message ->
                            dialogMessage.value = message  // Update the popup message
                            showDialogMessage.value = true  // Show the popup
                            isSuccess.value = success
                        })
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCancelDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

fun formatRupiah(amount: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}