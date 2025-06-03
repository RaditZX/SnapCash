package com.example.snapcash.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.snapcash.data.Category
import com.example.snapcash.data.SessionManager
import com.example.snapcash.data.SnapCashApiService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val apiService: SnapCashApiService) : ViewModel() {
    val categories = mutableStateOf<List<Category>>(emptyList())
    val categoryById = mutableStateOf<Category?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun getAllCategories(search: String? = null, isPengeluaran: Boolean? = null) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                if (SessionManager.idToken.isNullOrBlank()) {
                    errorMessage.value = "Autentikasi gagal: Token tidak tersedia"
                    isLoading.value = false
                    return@launch
                }

                val response = apiService.getAllCategories("Bearer ${SessionManager.idToken}", search, isPengeluaran)
                Log.d("CATEGORY_RESPONSE", "Response: $response") // Log respon untuk debugging

                if (response.isSucces) {
                    categories.value = response.data.mapNotNull { json ->
                        try {
                            Log.d("CATEGORY_JSON", "Parsing item: $json")

                            val id = json.get("id")?.asString ?: run {
                                Log.w("CATEGORY_PARSING", "Skipping item due to missing 'id': $json")
                                return@mapNotNull null
                            }
                            val nama = json.get("nama")?.asString ?: run {
                                Log.w("CATEGORY_PARSING", "Skipping item due to missing 'nama': $json")
                                return@mapNotNull null
                            }
                            val isPengeluaran = json.get("isPengeluaran")?.asBoolean ?: run {
                                Log.w("CATEGORY_PARSING", "Skipping item due to missing 'isPengeluaran': $json")
                                return@mapNotNull null
                            }

                            Category(
                                id = id,
                                nama = nama,
                                isPengeluaran = isPengeluaran,
                                userId = json.get("userId")?.asString,
                                createdAt = json.get("createdAt")?.asString,
                                updatedAt = json.get("updatedAt")?.asString
                            )
                        } catch (e: Exception) {
                            Log.e("CATEGORY_PARSING", "Failed to parse category: $json, error: ${e.message}", e)
                            null // Skip entri yang gagal diparsing
                        }
                    }
                    errorMessage.value = if (categories.value.isEmpty()) "Tidak ada kategori ditemukan" else null
                } else {
                    errorMessage.value = response.message ?: "Gagal mengambil daftar kategori"
                }
            } catch (e: Exception) {
                Log.e("CATEGORY_EXCEPTION", "Exception: ${e.message}", e)
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getCategoryById(id: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.getCategoryById("Bearer ${SessionManager.idToken}", id)
                if (response.isSucces) {
                    val json = response.data
                    if (json != null) {
                        categoryById.value = Category(
                            id = json.get("id")?.asString ?: "",
                            nama = json.get("nama")?.asString ?: "",
                            isPengeluaran = json.get("isPengeluaran")?.asBoolean ?: false,
                            userId = json.get("userId")?.asString,
                            createdAt = json.get("createdAt")?.asString,
                            updatedAt = json.get("updatedAt")?.asString
                        )
                    }
                    errorMessage.value = null
                } else {
                    errorMessage.value = response.message ?: "Gagal mengambil detail kategori"
                }
            } catch (e: Exception) {
                Log.e("CATEGORY_EXCEPTION", "Exception: ${e.message}", e)
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addCategory(category: Category, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val data = JsonObject().apply {
                    addProperty("nama", category.nama)
                    addProperty("isPengeluaran", category.isPengeluaran)
                }
                val response = apiService.addCategory("Bearer ${SessionManager.idToken}", data)
                if (response.isSucces) {
                    getAllCategories() // Refresh daftar kategori
                    navController.navigate("kategori")
                    errorMessage.value = "Kategori berhasil ditambahkan" // Feedback sukses
                } else {
                    errorMessage.value = response.message ?: "Gagal menambahkan kategori"
                }
            } catch (e: Exception) {
                Log.e("CATEGORY_EXCEPTION", "Exception: ${e.message}", e)
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateCategory(id: String, category: Category, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val data = JsonObject().apply {
                    addProperty("nama", category.nama)
                    addProperty("isPengeluaran", category.isPengeluaran)
                }
                val response = apiService.updateCategory("Bearer ${SessionManager.idToken}", id, data)
                if (response.isSucces) {
                    getAllCategories() // Refresh daftar kategori
                    navController.navigate("kategori")
                    errorMessage.value = "Kategori berhasil diperbarui" // Feedback sukses
                } else {
                    errorMessage.value = response.message ?: "Gagal memperbarui kategori"
                }
            } catch (e: Exception) {
                Log.e("CATEGORY_EXCEPTION", "Exception: ${e.message}", e)
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteCategory(id: String, navController: NavController) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val response = apiService.deleteCategory("Bearer ${SessionManager.idToken}", id)
                Log.d("DELETE_RESPONSE", "Response: $response")
                if (response.isSucces) {
                    getAllCategories()
                    navController.navigate("kategori")
                    errorMessage.value = "Kategori berhasil dihapus"
                } else {
                    errorMessage.value = response.message ?: "Gagal menghapus kategori"
                }
            } catch (e: retrofit2.HttpException) {
                Log.e("DELETE_HTTP_EXCEPTION", "HTTP Error: ${e.code()} - ${e.message}", e)
                errorMessage.value = when (e.code()) {
                    500 -> "Terjadi kesalahan di server, silakan coba lagi nanti"
                    401 -> "Sesi habis, silakan login kembali"
                    404 -> "Kategori tidak ditemukan"
                    else -> "Gagal menghapus kategori: ${e.message}"
                }
                if (e.code() == 401) {
                    navController.navigate("signIn")
                }
            } catch (e: Exception) {
                Log.e("DELETE_EXCEPTION", "Exception: ${e.message}", e)
                errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}