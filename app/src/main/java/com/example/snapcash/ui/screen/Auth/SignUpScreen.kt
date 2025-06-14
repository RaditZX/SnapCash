package com.example.snapcash.ui.screen.Auth

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.snapcash.R
import com.example.snapcash.ViewModel.AuthViewModel
import com.example.snapcash.client.GoogleAuthUiClient
import com.example.snapcash.ui.component.ModernAlertDialog

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPerfettoTraceProcessorApi::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val ConfirmationPasswordState = remember { mutableStateOf("") }

    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    val context = LocalContext.current
    val authClient = remember { GoogleAuthUiClient(context) }

    val isLoading by viewModel.isLoading
    val isSuccess by viewModel.isSucces

    val nextRoute = remember { mutableStateOf("") }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        authClient.getIdTokenFromIntent(result.data) { idToken ->
            Log.d("token", idToken.toString())
            if (idToken != null) {

                viewModel.registerWithGoogle(idToken, onResult = { success, message ->
                    dialogMessage.value = message  // Update the popup message
                    showDialog.value = true  // Show the popu
                    nextRoute.value = "signin"
                })

            } else {
                Log.e("GoogleLogin", "Gagal mendapatkan ID Token")
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0F13))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.snapcash),
                contentDescription = "App Logo",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Make Your Account",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your email and password to SignUp",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = ConfirmationPasswordState.value,
                onValueChange = { ConfirmationPasswordState.value = it },
                label = { Text("Confirmation Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.signUp(
                        email = emailState.value,
                        password = passwordState.value,
                        confirmPassword = ConfirmationPasswordState.value,
                        onResult = { success, message ->
                            dialogMessage.value = message
                            showDialog.value = true
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(text = "Register", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Or",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authClient.signOut {
                        launcher.launch(authClient.getSignInIntent())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Continue with Google", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Already Have Account?", color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SignIn",
                    color = Color(0xFF4D81E7),
                    modifier = Modifier.clickable { navController.navigate("signIn") }
                )
            }
        }

        // ✅ Loading Overlay — on top of all UI
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

        // ✅ Dialog — also on top of all UI
        if (showDialog.value) {
            ModernAlertDialog(
                showDialog,
                "Register",
                dialogMessage.value,
                if (isSuccess) {
                    if (nextRoute.value == "dashboard") "dashboard" else "SignIn"
                } else {
                    "signUp"
                },
                navController
            )
        }

    }
}

