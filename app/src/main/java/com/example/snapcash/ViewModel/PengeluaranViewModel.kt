package com.example.snapcash.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.snapcash.data.FilterModel
import com.example.snapcash.data.SessionManager
import com.example.snapcash.data.SnapCashApiService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
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

    fun getPengeluaranUser(filterData: FilterModel, searchQuery: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                Log.d("date", filterData.startDate)
                val response = apiService.getPengeluaranUser("Bearer ${SessionManager.idToken}",filterData.kategori,
                    filterData.startDate,
                    filterData.endDate,
                    filterData.min,
                    filterData.max,
                    searchQuery
                )
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

    fun updatePengeluaranUserById(id: String, data: JsonObject, navController: NavController,onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response =
                    apiService.updatePengeluaranById("Bearer ${SessionManager.idToken}", id, data)
                onResult(true, response.message)
            }catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    try {
                        JSONObject(it).getString("message") // Extract the "message" field
                    } catch (ex: Exception) {
                        "Unknown HTTP error"
                    }
                } ?: "Unknown HTTP error"

                Log.e("auth", "HttpException: $errorMessage")
                onResult(false, "Error: $errorMessage")
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addPengeluaran(data: JsonObject, navController: NavController, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.addPengeluaran("Bearer ${SessionManager.idToken}", data)
                onResult(true, response.message)
            }catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    try {
                        JSONObject(it).getString("message") // Extract the "message" field
                    } catch (ex: Exception) {
                        "Unknown HTTP error"
                    }
                } ?: "Unknown HTTP error"

                Log.e("auth", "HttpException: $errorMessage")
                onResult(false, "Error: $errorMessage")
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deletePengeluaranById(id: String, navController: NavController,onResult: (Boolean, String) -> Unit){
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.deletePengeluaranById("Bearer ${SessionManager.idToken}", id)
                onResult(true, response.message)
            }catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody?.let {
                    try {
                        JSONObject(it).getString("message") // Extract the "message" field
                    } catch (ex: Exception) {
                        "Unknown HTTP error"
                    }
                } ?: "Unknown HTTP error"

                Log.e("auth", "HttpException: $errorMessage")
                onResult(false, "Error: $errorMessage")
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}
