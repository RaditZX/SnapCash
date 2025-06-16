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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import com.example.snapcash.ViewModel.CategoryViewModel

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
                    selectedTabIndex = 0,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[0]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = true,
                        onClick = {},
                        text = { Text("INCOME", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) }
                    )
                    Tab(
                        selected = false,
                        onClick = { navController.navigate("tambah/pengeluaran") },
                        text = { Text("OUTCOME", color = MaterialTheme.colorScheme.onSurfaceVariant) }
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
                        onClick = { viewModel.deletePemasukanById(id.toString(), navController,onResult = { success, message ->
                            dialogMessage.value = message  // Update the popup message
                            showDialog.value = true  // Show the popup
                            isSuccess.value = success
                        }) },
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Pemasukan",
                            tint = MaterialTheme.colorScheme.onPrimary
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
                    Text("Total Income", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                    Text(
                        formatRupiah(total.toInt()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
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
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Title", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            item {
                OutlinedTextField(
                    value = sumber,
                    onValueChange = { sumber = it },
                    label = { Text("Source", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
            item {
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
                        label = { Text("Date", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier
                            .weight(1f)
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        onClick = {
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
                    Text("There are no additional add", color = MaterialTheme.colorScheme.onSurfaceVariant)
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

        // 🔄 Overlay Loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
            title = { Text("Konfirmasi", color = MaterialTheme.colorScheme.onBackground) },
            text = { Text("Apakah Anda yakin ingin kembali?", color = MaterialTheme.colorScheme.onBackground) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePemasukanById(id.toString(), navController,onResult = { success, message ->
                            dialogMessage.value = message  // Update the popup message
                            showDialog.value = true  // Show the popup
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

