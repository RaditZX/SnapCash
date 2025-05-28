package com.example.snapcash.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
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
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GenerateFromInvoiceViewModel  @Inject constructor(private val apiService: SnapCashApiService) : ViewModel() {

    var isLoading = mutableStateOf(false)
    var data = mutableStateOf(JsonObject())
    var isSucces = mutableStateOf(false)
    val message = mutableStateOf("")

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    fun setData(newData: JsonObject) {
        data.value = newData
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun addPengeluaranOrPemasukanByGPT(imageFile: File, onResult: (Boolean, String) -> Unit){
        viewModelScope.launch {
            setLoading(true)
            try {
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                    MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

                val response = apiService.addPengeluaranOrPemasukanByGPT("Bearer ${SessionManager.idToken}"
                    ,imagePart)
                message.value = response.message

                if (response.isSucces) {
                    val body = response.data
                    setData(response.data)
                    if (!body.entrySet().isEmpty()) {
                        Log.d("API_SUCCESS", "Message: ${response.message}, Data: ${response.data}")
                        onResult(true, response.message)
                        isSucces.value = true
                    } else {
                        Log.e("FAIL", "Message: ${response.message}")
                        onResult(false, response.message)
                    }
                } else {
                    Log.e("ERROR", "Message: ${response.message}")
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                setLoading(false)
                var errorMsg = "Unknown error occurred"
                if (e is HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("HTTP_EXCEPTION", "Raw error body: $errorBody")

                    errorBody?.let {
                        try {
                            val json = JSONObject(it)
                            errorMsg = json.optString("message", errorMsg)
                        } catch (jsonEx: JSONException) {
                            Log.e("PARSE_ERROR", "Failed to parse error body", jsonEx)
                        }
                    }
                } else {
                    errorMsg = e.message ?: errorMsg
                }

                Log.e("EXCEPTION", "Exception: $errorMsg", e)
                onResult(false, "failed: $errorMsg")
            } finally {
                setLoading(false)
            }
        }
    }

}