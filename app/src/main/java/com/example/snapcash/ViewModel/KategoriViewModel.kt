package com.example.snapcash.ViewModel;

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
class KategoriViewModel @Inject constructor(private val apiService: SnapCashApiService) : ViewModel() {
    val kategoriList = mutableStateOf(arrayOf<JsonObject>())
    val kategoriById = mutableStateOf(JsonObject())
    val isLoading = mutableStateOf(false)

    fun setKategoriList(data: Array<JsonObject>) {
        kategoriList.value = data
    }

    fun getKategori() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.getKategori("Bearer ${SessionManager.idToken}")
                if (response.isSucces) {
                    setKategoriList(response.data)
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getKategoriById(id: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.getKategoriById("Bearer ${SessionManager.idToken}", id)
                if (response.isSucces) {
                    kategoriById.value = response.data
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addKategori(data: JsonObject, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.addKategori("Bearer ${SessionManager.idToken}", data)
                if (response.isSucces) {
                    navController.navigate("kategori")
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateKategori(id: String, data: JsonObject, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.updateKategori("Bearer ${SessionManager.idToken}", id, data)
                if (response.isSucces) {
                    navController.navigate("kategori")
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteKategori(id: String, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.deleteKategori("Bearer ${SessionManager.idToken}", id)
                if (response.isSucces) {
                    navController.navigate("kategori")
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}
