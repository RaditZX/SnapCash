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
class PemasukanViewModel @Inject constructor(private val apiService: SnapCashApiService) :
    ViewModel() {
    val pemasukanData = mutableStateOf(arrayOf<JsonObject>())

    val pemasukanDataById = mutableStateOf(JsonObject())

    fun setPemasukanData(data: Array<JsonObject>) {
        pemasukanData.value = data
    }

    val isLoading = mutableStateOf(false)

    fun getPemasukanUser(filterData: FilterModel, searchQuery: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getPemasukanUser(
                    "Bearer ${SessionManager.idToken}",
                    filterData.kategori,
                    filterData.startDate,
                    filterData.endDate,
                    filterData.min,
                    filterData.max,
                    searchQuery
                )

                if (response.isSucces) {
                    setPemasukanData(response.data)
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getPemasukanUserById(id: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response =
                    apiService.getPemasukanUserById("Bearer ${SessionManager.idToken}", id)
                if (response.isSucces) {
                    pemasukanDataById.value = response.data
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updatePemasukanUserById(id: String, data: JsonObject, navController: NavController, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response =
                    apiService.updatePemasukanById("Bearer ${SessionManager.idToken}", id, data)
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

    fun addPemasukan(data: JsonObject, navController: NavController,  onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.addPemasukan("Bearer ${SessionManager.idToken}", data)

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
            }
            catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deletePemasukanById(id: String, navController: NavController,onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response =
                    apiService.deletePemasukanById("Bearer ${SessionManager.idToken}", id)
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