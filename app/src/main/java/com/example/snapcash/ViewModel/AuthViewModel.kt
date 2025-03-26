package com.example.snapcash.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcash.data.SignInRequest
import com.example.snapcash.data.SnapCashApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel  @Inject constructor (private val apiService: SnapCashApiService) : ViewModel() {
    fun signUp(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.signUp(SignInRequest(email, password))
                onResult(true, response.message)  // Handle success (e.g., store token)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.signIn(SignInRequest(email, password))
                onResult(true, response.message)  // Handle success (e.g., store token)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Unknown error")
            }
        }
    }
}

