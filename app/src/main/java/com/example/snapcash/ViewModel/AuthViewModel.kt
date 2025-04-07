package com.example.snapcash.ViewModel


import retrofit2.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcash.data.SignInRequest
import com.example.snapcash.data.SnapCashApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val apiService: SnapCashApiService) : ViewModel() {

    var isLoading = mutableStateOf(false)

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun signUp(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.signUp(SignInRequest(email, password))
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
            }catch (e: Exception) {
                // This handles other unexpected exceptions
                Log.e("auth", "Exception: ${e.message ?: "Unknown error"}")
                onResult(false, "Exception: ${e.message ?: "Unknown error"}")
            }finally {
                setLoading(false)
            }
        }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.signIn(SignInRequest(email, password))

                // If response.message contains a specific error, throw a custom exception
                if (response.message == "Invalid credentials") {
                    throw Exception("Incorrect email or password.")
                }

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
            }  catch (e: Exception) {
                // This handles other unexpected exceptions
                Log.e("auth", "Exception: ${e.message ?: "Unknown error"}")
                onResult(false, "Exception: ${e.message ?: "Unknown error"}")
            }finally {
                setLoading(false)
            }
        }
    }

    fun registerWithGoogle(idToken: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            try {
                Log.d("auth", idToken)
                val response = apiService.registerWithGoogle("Bearer $idToken")
                onResult(true, response.message)
            } catch (e: HttpException) {
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
                onResult(false, "Exception: ${e.localizedMessage}")
            }finally {
                setLoading(false)
            }
        }
    }

    fun signWithGoogle(idToken: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            try {
                Log.d("auth", idToken)
                val response = apiService.signWithGoogle("Bearer $idToken")

                onResult(true, response.message)
            } catch (e: HttpException) {
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
                onResult(false, "Exception: ${e.localizedMessage}")
            }finally {
                setLoading(false)
            }
        }
    }

}
