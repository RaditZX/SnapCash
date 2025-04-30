package com.example.snapcash.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.snapcash.data.SessionManager
import com.example.snapcash.data.SnapCashApiService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengeluaranViewModel @Inject constructor(private val apiService: SnapCashApiService) :
    ViewModel() {
    val pengeluaranData = mutableStateOf(arrayOf<JsonObject>())

    fun setPengeluaranData(data: Array<JsonObject>) {
        pengeluaranData.value = data
    }

    val pengeluaranDataById = mutableStateOf(JsonObject())

    val isLoading = mutableStateOf(false)

    fun getPengeluaranUser() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.getPengeluaranUser("Bearer ${SessionManager.idToken}")
                if (response.isSucces) {
                    setPengeluaranData(response.data)
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getPengluaranUserById(id: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response =
                    apiService.getPengeluaranUserById("Bearer ${SessionManager.idToken}", id)
                if (response.isSucces) {
                    pengeluaranDataById.value = response.data
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updatePengeluaranUserById(id: String, data: JsonObject, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response =
                    apiService.updatePengeluaranById("Bearer ${SessionManager.idToken}", id, data)
                if (response.isSucces) {
                    navController.navigate("history")
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addPengeluaran(data: JsonObject, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.addPengeluaran("Bearer ${SessionManager.idToken}", data)
                if (response.isSucces) {
                    navController.navigate("history")
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deletePengeluaranById(id: String, navController: NavController){
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.deletePengeluaranById("Bearer ${SessionManager.idToken}", id)
                if (response.isSucces) {
                    navController.navigate("history")
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}
