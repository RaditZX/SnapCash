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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.example.snapcash.data.Tambahanbiaya
import com.example.snapcash.ui.component.AddBiayaDialog
import com.example.snapcash.ui.component.CurrencyInputField
import com.example.snapcash.ui.component.DropdownMenu
import com.example.snapcash.ui.component.ModernAlertDialog
import com.example.snapcash.ui.theme.night
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PemasukanEntryScreen(
    navController: NavController,
    viewModel: PemasukanViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel(),
    id: String?,
    preview: Boolean
) {
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }

    var judul by remember { mutableStateOf("") }
    var sumber by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf(0) }
    var subTotal by remember { mutableStateOf(0) }
    var kategori by remember { mutableStateOf("") }
    var biayalist by remember { mutableStateOf(listOf<Tambahanbiaya>()) }
    val pemasukanData by remember { viewModel.pemasukanDataById }
    val kategoriList = listOf("Gaji", "Investasi", "Bisnis", "Hadiah")
    val categories by categoryViewModel.categories
    val allCategories = remember(categories) {
        (kategoriList + categories.filter { !it.isPengeluaran }.map { it.nama }).distinct()
    }
    val categoriesWithAddOption = remember(allCategories) {
        allCategories + "Tambah Kategori"
    }
    var showDialogBiaya by remember { mutableStateOf(false) }
    var isUpdate by remember { mutableStateOf(false) }
    var totalIsUpdate by remember { mutableStateOf(0.0) }

    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isSuccess = remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        categoryViewModel.getAllCategories()
    }

    if (id != null) {
        LaunchedEffect(Unit) {
            viewModel.getPemasukanUserById(id)
        }

        LaunchedEffect(pemasukanData) {
            if (pemasukanData.size() > 0) {
                isUpdate = true
                judul = pemasukanData.get("namaPemasukan")?.asString ?: ""
                kategori = pemasukanData.get("kategori")?.asString ?: ""
                sumber = pemasukanData.get("sumber")?.asString ?: ""
                tanggal = pemasukanData.get("tanggal")?.asString ?: ""
                nominal = pemasukanData.get("total")?.asInt ?: 0
                subTotal = pemasukanData.get("subTotal")?.asInt ?: 0

                val biayaJsonArray = pemasukanData.get("tambahanBiaya")?.asJsonArray
                biayalist = biayaJsonArray?.map { item ->
                    val obj = item.asJsonObject
                    Tambahanbiaya(
                        namabiaya = obj.get("namaBiaya").asString,
                        jumlahbiaya = obj.get("jumlah").asDouble,
                    )
                } ?: emptyList()

                totalIsUpdate = if (nominal == 0) {
                    0.0
                } else {
                    val totalBiaya = biayalist.sumOf { it.jumlahbiaya }
                    (nominal - totalBiaya)
                }
            }
        }
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
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp)) // Placeholder to align title
                    }
                    Text(
                        text = "CATAT",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // Balance the layout
                }

                TabRow(
                    selectedTabIndex = 0,
                    contentColor = Color(0xFF2D6CE9),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[0]),
                            color = Color(0xFF2D6CE9)
                        )
                    }
                ) {
                    Tab(
                        selected = true,
                        onClick = {},
                        text = { Text("INCOME", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = false,
                        onClick = { navController.navigate("tambah/pengeluaran") },
                        text = { Text("OUTCOME", color = Color.Gray) }
                    )
                }
            }
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (isUpdate) {
                    FloatingActionButton(
                        containerColor = Color(0xFF2D6CE9),
                        onClick = { viewModel.deletePemasukanById(id.toString(), navController,onResult = { success, message ->
                            dialogMessage.value = message  // Update the popup message
                            showDialog.value = true  // Show the popup
                            isSuccess.value = success
                        }) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Pemasukan"
                        )
                    }
                }
            }
        },
        bottomBar = {
            val nominalValue = nominal.toDouble()
            val totalTambahan = biayalist.sumOf { it.jumlahbiaya }
            val total = if (isUpdate) {
                totalIsUpdate + totalTambahan
            } else {
                nominalValue + totalTambahan
            }

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
                    Text("Total Income", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        formatRupiah(total.toInt()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {

                        val biayaArray = JsonArray()
                        biayalist.forEach {
                            val biayaObj = JsonObject().apply {
                                addProperty("namaBiaya", it.namabiaya)
                                addProperty("jumlah", it.jumlahbiaya)
                            }
                            biayaArray.add(biayaObj)
                        }

                        val request = JsonObject().apply {
                            addProperty("namaPemasukan", judul)
                            addProperty("sumber", sumber)
                            addProperty("tanggal", tanggal)
                            addProperty("total", total)
                            addProperty("subTotal", nominal)
                            addProperty("kategori", kategori)
                            add("tambahanBiaya", biayaArray)
                            addProperty("isPengeluaran", false)
                        }

                        if (isUpdate) {
                            viewModel.updatePemasukanUserById(id.toString(), request, navController,onResult = { success, message ->
                                dialogMessage.value = message  // Update the popup message
                                showDialog.value = true  // Show the popup
                                isSuccess.value = success
                            } )
                        } else {
                            viewModel.addPemasukan(
                                request, onResult = { success, message ->
                                    dialogMessage.value = message  // Update the popup message
                                    showDialog.value = true  // Show the popup
                                    isSuccess.value = success
                                },
                                navController = navController
                            )
                        }

                    },
                    colors = ButtonColors(
                        containerColor = Color(0xFF2D6CE9),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Gray,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = sumber,
                    onValueChange = { sumber = it },
                    label = { Text("Source") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            item {
                DropdownMenu(
                    label = "Kategori",
                    options = categoriesWithAddOption,
                    selectedOption = kategori,
                    onOptionSelected = { selected ->
                        if (selected == "Tambah Kategori") {
                            // Navigasi ke ListKategoriScreen
                            navController.navigate("kategori")
                        } else {
                            // Update kategori jika bukan opsi "Tambah Kategori"
                            kategori = selected
                        }
                    },
                    containerColor = night,
                    customOptionContent = { option ->
                        @Composable {
                            if (option == "Tambah Kategori") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(option, modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah Kategori",
                                        tint = Color(0xFF2D6CE9) // Warna biru untuk konsistensi
                                    )
                                }
                            } else {
                                Text(option, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CurrencyInputField(
                        "Nominal",
                        if (isUpdate) subTotal else nominal,
                        onValueChange = { nominal = it },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = {},
                        label = { Text("Date") },
                        modifier = Modifier
                            .weight(1f)
                            .pointerInput(Unit) { detectTapGestures { datePicker.show() } },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
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
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Additional Add",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
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
                    Text("There are no additional add", color = Color.Gray)
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
                        Text(
                            "${biaya.namabiaya} - ${formatRupiah(biaya.jumlahbiaya.toInt())}",
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = {
                        biayalist = biayalist.toMutableList().apply { removeAt(index) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
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
        if (showDialog.value) {
            ModernAlertDialog(
                showDialog,
                "Income",
                dialogMessage.value,
                if (isSuccess.value) "history" else null,
                navController
            )

        }
    }

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
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin ingin kembali?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePemasukanById(id.toString(), navController,onResult = { success, message ->
                            dialogMessage.value = message  // Update the popup message
                            showDialog.value = true  // Show the popup
                            isSuccess.value = success
                        })
                        showCancelDialog = false
                    }
                ) {
                    Text("Batal")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCancelDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

