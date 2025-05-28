package com.example.snapcash.ui.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.snapcash.ViewModel.AuthViewModel
import com.example.snapcash.ViewModel.currencyViewModel
import com.example.snapcash.ui.component.ModernAlertDialog
import com.google.gson.JsonObject
import java.io.File

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: currencyViewModel = hiltViewModel(),
    userViewModel: AuthViewModel = hiltViewModel(),
) {
    val userData by remember { userViewModel.userDatas }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }
    var url by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    val painter = if (url != Uri.EMPTY) {
        rememberAsyncImagePainter(model = url)
    } else {
        rememberAsyncImagePainter(model = userData.foto)
    }
    val currencyData by remember { viewModel.currencyList }
    val context = LocalContext.current
    val isLoading by userViewModel.isLoading
    val isSuccess by userViewModel.isSucces


    LaunchedEffect(Unit) {
        viewModel.getCurrency()
        userViewModel.getUserData()
    }

    LaunchedEffect(userData) {
        name = userData.username.toString()
        email = userData.email
        number = userData.no_hp.toString()
        currency = userData.currencyChoice.toString()
    }

    Log.d("email", userData.email.toString())
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    val request = JsonObject().apply {
        addProperty("username", name)
        addProperty("currencyChoice", currency)
        addProperty("no_hp", number)
//        addProperty("photo", uriToFile(context, url))
    }

    val currencyList = currencyData.map {

        it.get("currency_code")?.asString ?: ""

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F13)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp) // Pastikan tinggi cukup untuk offset
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF2D6CE9), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(bottomStart = 100.dp, bottomEnd = 100.dp)
                    )
            )


            Box( // Box luar
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 60.dp) // offset DI SINI, bukan di dalam!
            ) {
                // Lingkaran foto
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.Gray) // bantu debug
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(userData.foto),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(80.dp))
        LazyColumn(modifier = Modifier.padding(horizontal = 24.dp)) {
            item {
                EditProfileField(Icons.Default.Person, "Name", name.toString()) { name = it }
                EditProfileField(Icons.Default.Email, "Email", email.toString()) { email = it }
                EditProfileField(Icons.Default.Phone, "Number", number.toString()) { number = it }

                EditProfileImageField(
                    icon = Icons.Default.Person,
                    label = "Photo"
                ) { uri ->
                    url = uri
                }

                EditProfileDropdownField(
                    icon = Icons.Default.ShoppingCart,
                    label = "Currency",
                    options = currencyList,
                    selectedOption = currency.toString()
                ) { selected ->
                    currency = selected
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {

                        userViewModel.updateUserData(
                            data = request,
                            photo = uriToFile(context, url),
                            onResult = { success, message ->
                                dialogMessage.value = message
                                showDialog.value = true
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                ) {
                    Text("Save", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
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
                "UpdateProfile",
                dialogMessage.value,
                if (isSuccess) "profile" else "profile/edit",
                navController
            )

        }
    }

}

@Composable
fun EditProfileField(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color(0xFF2979FF),
                unfocusedIndicatorColor = Color.Gray,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDropdownField(
    icon: ImageVector,
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedOption,
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color(0xFF2979FF),
                    unfocusedIndicatorColor = Color.Gray,
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption, color = Color.White) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun EditProfileImageField(
    icon: ImageVector,
    label: String,
    onImagePicked: (Uri) -> Unit  // Callback untuk menangani pemilihan gambar
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImagePicked(uri) // Callback dengan URI gambar
        } ?: run {
            Toast.makeText(context, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    pickImageLauncher.launch("image/*")
                }
                .padding(16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            // Placeholder: tampilkan ikon saat tidak ada gambar yang dipilih
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Select Image",
                tint = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
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

