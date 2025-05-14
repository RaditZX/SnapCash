package com.example.snapcash.ui.screen.Upload

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.GenerateFromInvoiceViewModel
import com.example.snapcash.ViewModel.PemasukanViewModel
import com.example.snapcash.ViewModel.PengeluaranViewModel
import com.example.snapcash.data.Barang
import com.example.snapcash.data.Tambahanbiaya
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.snapcash.ui.component.ModernAlertDialog

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavController,
    viewModel: GenerateFromInvoiceViewModel = hiltViewModel(),
    viewModel1: PemasukanViewModel = hiltViewModel(),
    viewModel2: PengeluaranViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor = remember { Executors.newSingleThreadExecutor() }
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var fileToSend by remember { mutableStateOf<File?>(null) }
    val scope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading
    val isSuccess = remember { mutableStateOf(false) }

    val pengeluaranData by remember { viewModel2.pengeluaranDataById }
    val pemasukanData by remember { viewModel1.pemasukanDataById }

    var barangList by remember { mutableStateOf(listOf<Barang>()) }
    var biayalist by remember { mutableStateOf(listOf<Tambahanbiaya>()) }
    var judul by remember { mutableStateOf("") }
    var toko by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var total by remember { mutableStateOf(0) }
    var kategori by remember { mutableStateOf("")}
    var sumber by remember { mutableStateOf("") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) {
                scope.launch {
                    viewModel.addPengeluaranOrPemasukanByGPT(file, onResult = { success, message ->
                        dialogMessage.value = message  // Update the popup message
                        showDialog.value = true  // Show the popup
                    })
                }
            } else {
                Toast.makeText(context, "Gagal mengambil file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(fileToSend) {
        fileToSend?.let { file ->
            viewModel.addPengeluaranOrPemasukanByGPT(file,onResult = { success, message ->
                isSuccess.value = success
                dialogMessage.value = message  // Update the popup message
                showDialog.value = true  // Show the popup
            })
            fileToSend = null // reset setelah diproses
        }
    }

    if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 35.dp)
            ) {
                AndroidView(factory = { ctx ->
                    PreviewView(ctx).apply {
                        previewView = this
                        post {
                            startCamera(
                                context,
                                lifecycleOwner,
                                cameraProviderFuture,
                                imageCapture,
                                executor,
                                this
                            )
                        }
                    }
                }, modifier = Modifier.weight(0.1f))

                Button(
                    onClick = {
                        takePicture(context, imageCapture, executor) { savedFile ->
                            fileToSend = savedFile
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF2D6CE9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Take Invoice", color = Color.White)
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF2D6CE9))
                ) {
                    Text(text = "Upload Invoice",color = Color.White)
                }

                if (showDialog.value) {
                    if (!isSuccess.value) {
                        ModernAlertDialog(
                            showDialog,
                            "Invoice Capture",
                            dialogMessage.value,
                            "camera",
                            navController
                        )
                    } else {
                        // Ambil ID dan tipe data (pengeluaran/pemasukan)
                        val id = viewModel.data.value.get("id")?.asString ?: ""
                        val isPengeluaran = viewModel.data.value.get("isPengeluaran")?.asBoolean ?: true

                        if (isPengeluaran) {
                            if (id.isNotEmpty()) {
                                LaunchedEffect(Unit) {
                                    viewModel2.getPengluaranUserById(id)
                                }
                                LaunchedEffect(pengeluaranData) {
                                    if (pengeluaranData.size() > 0) {
                                        judul = pengeluaranData.get("namaPengeluaran")?.asString ?: ""
                                        toko = pengeluaranData.get("toko")?.asString ?: ""
                                        tanggal = pengeluaranData.get("tanggal")?.asString ?: ""
                                        kategori = pengeluaranData.get("kategori")?.asString ?: ""
                                        total = pengeluaranData.get("total").asInt

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

                            PengeluaranDialog(
                                navController = navController,
                                id = id,
                                judul = judul,
                                toko = toko,
                                tanggal = tanggal,
                                kategori = kategori,
                                total = total,
                                barangList = barangList,
                                biayalist = biayalist
                            )

                        } else {
                            if (id.isNotEmpty()) {
                                LaunchedEffect(Unit) {
                                    viewModel1.getPemasukanUserById(id)
                                }

                                LaunchedEffect(pemasukanData) {
                                    if (pemasukanData.size() > 0) {
                                        judul = pemasukanData.get("namaPemasukan")?.asString ?: ""
                                        kategori = pemasukanData.get("kategori")?.asString ?: ""
                                        sumber = pemasukanData.get("sumber")?.asString ?: ""
                                        tanggal = pemasukanData.get("tanggal")?.asString ?: ""
                                        total = pemasukanData.get("total")?.asInt ?: 0

                                        val biayaJsonArray = pemasukanData.get("tambahanBiaya")?.asJsonArray
                                        biayalist = biayaJsonArray?.map {
                                            val obj = it.asJsonObject
                                            Tambahanbiaya(
                                                namabiaya = obj.get("namaBiaya").asString,
                                                jumlahbiaya = obj.get("jumlah").asDouble,
                                            )
                                        } ?: emptyList()
                                    }
                                }
                            }

                            PemasukanDialog(
                                navController = navController,
                                id = id,
                                judul = judul,
                                toko = toko,
                                tanggal = tanggal,
                                kategori = kategori,
                                total = total,
                                biayalist = biayalist
                            )
                        }
                    }
                }
            }

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
        }

    } else {
        LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
    }
}

@Composable
fun PengeluaranDialog(
    navController: NavController,
    id: String,
    judul: String,
    toko: String,
    tanggal: String,
    kategori: String,
    total: Int,
    barangList: List<Barang>,
    biayalist: List<Tambahanbiaya>
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Pengeluaran Invoice", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                Text("Judul: $judul")
                Spacer(Modifier.height(4.dp))
                Text("Toko: $toko")
                Spacer(Modifier.height(4.dp))
                Text("Tanggal: $tanggal")
                Spacer(Modifier.height(4.dp))
                Text("Kategori: $kategori")
                Spacer(Modifier.height(4.dp))
                Text("Total: Rp $total", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                if (barangList.isNotEmpty()) {
                    Text("Barang:", style = MaterialTheme.typography.titleSmall)
                    barangList.forEach { barang ->
                        Text("- ${barang.nama}: ${barang.jumlah} x Rp ${barang.harga}")
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (biayalist.isNotEmpty()) {
                    Text("Biaya Tambahan:", style = MaterialTheme.typography.titleSmall)
                    biayalist.forEach { biaya ->
                        Text("- ${biaya.namabiaya}: Rp ${biaya.jumlahbiaya}")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { navController.navigate("update/pengeluaran/$id") }) {
                Text("Edit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { navController.navigate("history") }) {
                Text("Lanjut")
            }
        }
    )
}

@Composable
fun PemasukanDialog(
    navController: NavController,
    id: String,
    judul: String,
    toko: String,
    tanggal: String,
    kategori: String,
    total: Int,
    biayalist: List<Tambahanbiaya>
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Pemasukan Invoice", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text("Judul: $judul")
                Spacer(Modifier.height(4.dp))
                Text("Toko: $toko")
                Spacer(Modifier.height(4.dp))
                Text("Tanggal: $tanggal")
                Spacer(Modifier.height(4.dp))
                Text("Kategori: $kategori")
                Spacer(Modifier.height(4.dp))
                Text("Total: Rp $total", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                if (biayalist.isNotEmpty()) {
                    Text("Biaya Tambahan:", style = MaterialTheme.typography.titleSmall)
                    biayalist.forEach { biaya ->
                        Text("- ${biaya.namabiaya}: Rp ${biaya.jumlahbiaya}")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { navController.navigate("update/pemasukan/$id") }) {
                Text("Edit")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { navController.navigate("history") }) {
                Text("Lanjut")
            }
        }
    )
}


private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    previewView: PreviewView
) {
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        } catch (e: Exception) {
            Log.e("CameraX", "Failed to bind camera use cases", e)
        }
    }, ContextCompat.getMainExecutor(context))
}

fun compressImageFile(file: File, quality: Int = 50): File {
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
    val compressedFile = File(file.parent, "compressed_${file.name}")
    FileOutputStream(compressedFile).use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
    }
    return compressedFile
}


fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onImageSaved: (File) -> Unit
) {
    val file = File(
        context.externalMediaDirs.first(),
        "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            Handler(Looper.getMainLooper()).post {
                val compressedFile = compressImageFile(file, quality = 50) // Kompres ke 50% kualitas
                onImageSaved(compressedFile)
            }
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraX", "Photo capture failed", exception)
        }
    })
}


fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File.createTempFile("invoice_", ".jpg", context.cacheDir)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}