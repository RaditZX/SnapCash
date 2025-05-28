package com.example.snapcash.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.snapcash.ViewModel.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {

    val userData by remember { viewModel.userDatas }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isSucces = remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.getUserData()
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
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            ProfileField(Icons.Default.Person, "Name", userData.username.toString())
            ProfileField(Icons.Default.Email, "Email", userData.email.toString())
            ProfileField(Icons.Default.Phone, "Number", userData.no_hp.toString())
            ProfileField(Icons.Default.ShoppingCart, "Currency", userData.currencyChoice.toString())

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("profile/edit") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
            ) {
                Text("Edit Profile", color = Color.White)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Button(
                onClick = {
                    viewModel.signOut(onResult = { success, message ->
                        dialogMessage.value = message
                        showDialog.value = true
                        isSucces.value = success

                        if (success) {
                            navController.navigate("signIn") {
                                popUpTo("home") { inclusive = true } // opsional, hapus history
                            }
                        }
                    })},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Sign Out", color = Color.White)
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


    }
}

@Composable
fun ProfileField(icon: ImageVector, label: String, value: String) {
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
        Text(
            text = value,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(bottom = 4.dp)
        )
    }
}

