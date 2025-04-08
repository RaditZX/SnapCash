package com.example.snapcash.ui.screen.Upload


import android.Manifest
import android.content.Context
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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.ViewModel.GenerateFromInvoiceViewModel
import com.example.snapcash.ui.component.ModernAlertDialog
import com.google.accompanist.permissions.isGranted
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavController,viewModel: GenerateFromInvoiceViewModel = hiltViewModel()) {
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
    Log.d("data",viewModel.data.value.get("namaPengeluaran")?.asString ?: ""
    )

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) {
                scope.launch {
                    viewModel.addPengeluaranOrPemasukanByGPT(file,onResult = { success, message ->
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Take Invoice")
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Upload Invoice")
                }

                if (showDialog.value) {
                    ModernAlertDialog(
                        showDialog,
                        "Invoice Capture",
                        dialogMessage.value,
                        "history",
                        navController
                    )
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
                onImageSaved(file) // ← kirim balik ke composable
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
