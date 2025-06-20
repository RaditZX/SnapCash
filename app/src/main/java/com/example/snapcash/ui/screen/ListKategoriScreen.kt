package com.example.snapcash.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.CategoryViewModel
import com.example.snapcash.data.Category
import com.example.snapcash.ui.component.ModernAlertDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListKategoriScreen(navController: NavController, viewModel: CategoryViewModel = hiltViewModel()) {
    val categories by viewModel.categories
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    var searchQuery by remember { mutableStateOf("") }
    var isPengeluaranFilter by remember { mutableStateOf<Boolean?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Category?>(null) }
    var searchComplete by remember {mutableStateOf(false)}
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    LaunchedEffect(isPengeluaranFilter, searchComplete) {
        viewModel.getAllCategories(searchQuery.takeIf { it.isNotBlank() }, isPengeluaranFilter)
        searchComplete = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "KATEGORI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    TabRow(
                        selectedTabIndex = when (isPengeluaranFilter) {
                            true -> 1
                            false -> 2
                            null -> 0
                        },
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(
                                    tabPositions[when (isPengeluaranFilter) {
                                        true -> 1
                                        false -> 2
                                        null -> 0
                                    }]
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Tab(
                            selected = isPengeluaranFilter == null,
                            onClick = { isPengeluaranFilter = null },
                            text = { Text("Semua", fontWeight = if (isPengeluaranFilter == null) FontWeight.Bold else FontWeight.Normal, color = MaterialTheme.colorScheme.onBackground) }
                        )
                        Tab(
                            selected = isPengeluaranFilter == true,
                            onClick = { isPengeluaranFilter = true },
                            text = { Text("Pengeluaran", fontWeight = if (isPengeluaranFilter == true) FontWeight.Bold else FontWeight.Normal, color = MaterialTheme.colorScheme.onBackground) }
                        )
                        Tab(
                            selected = isPengeluaranFilter == false,
                            onClick = { isPengeluaranFilter = false },
                            text = { Text("Pemasukan", fontWeight = if (isPengeluaranFilter == false) FontWeight.Bold else FontWeight.Normal, color = MaterialTheme.colorScheme.onBackground) }
                        )
                    }
                }
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Cari Kategori", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { searchComplete = true }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "List Kategori",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            IconButton(
                                onClick = { showAddDialog = true } // ✅ gunakan variabel yang benar
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Kategori",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                if (categories.isEmpty()) {
                    Text(
                        text = "Tidak ada kategori",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(categories) { _, category ->
                            CategoryItem(
                                category = category,
                                onEdit = { showEditDialog = category },
                                onDelete = { viewModel.deleteCategory(category.id, navController) }
                            )
                        }
                    }
                }
            }
        }
    }

    CategoryDialog(
        showDialog = showAddDialog,
        onDismiss = { showAddDialog = false },
        onSave = { nama, isPengeluaran ->
            viewModel.addCategory(
                Category(id = "", nama = nama, isPengeluaran = isPengeluaran),
                navController,
                onResult = { success, message ->
                    dialogMessage.value = message
                    showDialog.value = true
                }
            )
        }
    )

    showEditDialog?.let { category ->
        CategoryDialog(
            showDialog = true,
            category = category,
            onDismiss = { showEditDialog = null },
            onSave = { nama, isPengeluaran ->
                viewModel.updateCategory(
                    category.id,
                    Category(id = category.id, nama = nama, isPengeluaran = isPengeluaran),
                    navController,
                    onResult = { success, message ->
                        dialogMessage.value = message
                        showDialog.value = true
                    }
                )
            }
        )
    }

    if (showDialog.value) {
        ModernAlertDialog(
            showDialog,
            "Kategori",
            dialogMessage.value,
            null,
            navController
        )
    }
}

@Composable
fun CategoryItem(category: Category, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                text = category.nama,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (category.isPengeluaran) "Pengeluaran" else "Pemasukan",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CategoryDialog(
    showDialog: Boolean,
    category: Category? = null,
    onDismiss: () -> Unit,
    onSave: (nama: String, isPengeluaran: Boolean) -> Unit
) {
    if (showDialog) {
        var nama by remember { mutableStateOf(category?.nama ?: "") }
        var isPengeluaran by remember { mutableStateOf(category?.isPengeluaran ?: false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(if (category == null) "Tambah Kategori" else "Edit Kategori", color = MaterialTheme.colorScheme.onBackground) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    var errorText by remember { mutableStateOf<String?>(null) }

                    OutlinedTextField(
                        value = nama,
                        onValueChange = { newValue ->
                            nama = newValue
                            errorText = when {
                                newValue.isBlank() -> "Nama kategori tidak boleh kosong atau hanya spasi"
                                !newValue.matches(Regex("^[a-zA-Z\\s]+\$")) -> "Nama hanya boleh berisi huruf dan spasi"
                                else -> null
                            }
                        },
                        label = { Text("Nama Kategori", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = errorText != null,
                        supportingText = {
                            if (errorText != null) {
                                Text(
                                    text = errorText!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        )
                    )

                    Column {
                        Text("Jenis Kategori", color = MaterialTheme.colorScheme.onBackground)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = isPengeluaran,
                                    onClick = { isPengeluaran = true },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text("Pengeluaran", modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = !isPengeluaran,
                                    onClick = { isPengeluaran = false },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text("Pemasukan", modifier = Modifier.padding(start = 4.dp), color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nama.isNotBlank()) {
                            onSave(nama, isPengeluaran)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Simpan", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDismiss) {
                    Text("Batal", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }

}