package com.example.snapcash.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcash.data.SessionManager
import com.example.snapcash.data.SnapCashApiService
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GenerateFromInvoiceViewModel  @Inject constructor(private val apiService: SnapCashApiService) : ViewModel() {

    var isLoading = mutableStateOf(false)
    var data = mutableStateOf(JsonObject())

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    fun setData(newData: JsonObject) {
        data.value = newData
    }

    fun addPengeluaranOrPemasukanByGPT(imageFile: File, onResult: (Boolean, String) -> Unit){
        viewModelScope.launch {
            setLoading(true)
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                    MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val response = apiService.addPengeluaranOrPemasukanByGPT("Bearer ${SessionManager.idToken}"
                    ,imagePart)

                if (response.isSucces) {
                    val body = response.data
                    setData(response.data)
                    if (!body.entrySet().isEmpty()) {
                        Log.d("API_SUCCESS", "Message: ${response.message}, Data: ${response.data}")
                        onResult(true, response.message)
                    } else {
                        Log.e("API_FAIL", "Message: ${response.message}")
                        onResult(false, response.message)
                    }
                } else {
                    Log.e("API_ERROR", "Message: ${response.message}")
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            } finally {
                setLoading(false) // Hide loading spinner
            }
        }
    }

}