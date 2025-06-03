package com.example.snapcash.ViewModel


import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcash.R
import com.example.snapcash.data.SessionManager
import com.example.snapcash.data.SignInRequest
import com.example.snapcash.data.SnapCashApiService
import com.example.snapcash.data.userData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val apiService: SnapCashApiService, application: Application) : AndroidViewModel(application){

    var isLoading = mutableStateOf(false)

    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }

    var isSucces = mutableStateOf(false)
    fun setIsSucces(succes: Boolean){
        isSucces.value = succes
    }

    var userDatas = mutableStateOf(userData(
        email = "",
        username = " ",
        currencyChoice = SessionManager.currencyChoice,
        foto = " ",
        no_hp = " "
    ))

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun signUp(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            try {
                val response = apiService.signUp(SignInRequest(email, password))
                if(response.isSucces){
                    setIsSucces(true)
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
                Log.d("token", response.data.userCredential._tokenResponse.idToken)
                // If response.message contains a specific error, throw a custom exception
                if (response.message == "Invalid credentials") {
                    throw Exception("Incorrect email or password.")
                }

                if(response.isSucces){
                    setIsSucces(true)
                    SessionManager.idToken = response.data.userCredential._tokenResponse.idToken
                    val responses= apiService.getUserData("Bearer ${SessionManager.idToken}")
                    SessionManager.currencyChoice = responses.data.currencyChoice
                    val currencyResponse = apiService.getCurrencyData("Bearer ${SessionManager.idToken}",SessionManager.currencyChoice.toString() )
                    SessionManager.locale = currencyResponse.data.locale
                    SessionManager.currencySymbol = currencyResponse.data.currency_symbol
                    SessionManager.loginMethodGoogle = false
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
                if(response.isSucces){
                    setIsSucces(true)

                    SessionManager.idToken = idToken
                }
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
                val response = apiService.signWithGoogle("Bearer $idToken")
                if(response.isSucces){
                    setIsSucces(true)
                    val responses= apiService.getUserData("Bearer ${SessionManager.idToken}")
                    SessionManager.currencyChoice = responses.data.currencyChoice
                    val currencyResponse = apiService.getCurrencyData("Bearer ${SessionManager.idToken}",SessionManager.currencyChoice.toString() )
                    SessionManager.locale = currencyResponse.data.locale
                    SessionManager.currencySymbol = currencyResponse.data.currency_symbol
                    SessionManager.loginMethodGoogle = true
                }
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

    fun getUserData() {
        viewModelScope.launch {
            setLoading(true)
            try {
                val token = SessionManager.idToken
                if (token.isNullOrEmpty()) {
                    Log.e("UserData", "Token is null or empty")
                    return@launch
                }

                val response = apiService.getUserData("Bearer $token")

                if (response.isSucces && response.data != null) {
                    userDatas.value = userData(
                        email = response.data.email ?: "",
                        foto = response.data.foto ?: "",
                        username = response.data.username ?: "",
                        currencyChoice = response.data.currencyChoice ?: "",
                        no_hp = response.data.no_hp ?: ""
                    )
                } else {
                    Log.e("UserData", "Failed to get user data: ${response.message}")
                }

            } catch (e: Exception) {
                Log.e("UserData", "Exception occurred: ${e.localizedMessage}")
            }finally {
                setLoading(false)
            }
        }
    }

    fun updateUserData(data: JsonObject, photo: File?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            setLoading(true)
            try {

                val username = data["username"]?.asString?.toRequestBody("text/plain".toMediaType())
                val currency = data["currencyChoice"]?.asString?.toRequestBody("text/plain".toMediaType())
                val number = data["no_hp"]?.asString?.toRequestBody("text/plain".toMediaType())

                val imagePart = photo?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photo", it.name, requestFile)
                }
                val response = apiService.updateUserData("Bearer ${SessionManager.idToken}", username, currency, number, imagePart)

                if (response.isSucces) {
                    setIsSucces(true)
                    Log.d("updateCurrenct", data["currencyChoice"]?.asString.toString())
                    SessionManager.currencyChoice = data["currencyChoice"]?.asString.toString()
                    val currencyResponse = apiService.getCurrencyData("Bearer ${SessionManager.idToken}",SessionManager.currencyChoice.toString() )
                    SessionManager.locale = currencyResponse.data.locale
                    SessionManager.currencySymbol = currencyResponse.data.currency_symbol
                    onResult(true, response.message ?: "Data berhasil diperbarui")
                } else {
                    onResult(false, response.message ?: "Gagal memperbarui data")
                }
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    400 -> "Permintaan tidak valid"
                    401 -> "Token tidak valid atau sesi habis"
                    500 -> "Terjadi kesalahan pada server"
                    else -> "Kesalahan tidak diketahui (${e.code()})"
                }
                onResult(false, errorMessage)
            } catch (e: IOException) {
                onResult(false, "Tidak dapat terhubung ke server. Periksa koneksi internet.")
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.localizedMessage}")
            }finally {
                setLoading(false)
            }
        }
    }

    fun signOut(onResult: (Boolean, String) -> Unit) {

        viewModelScope.launch {
            try {
                Log.d("auth", "Test")
                Log.d("auth", SessionManager.loginMethodGoogle.toString())

                if (SessionManager.loginMethodGoogle == true) {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    val user = firebaseAuth.currentUser

                    if (user == null) {
                        onResult(false, "User belum login")
                        return@launch
                    }
                    val context = getApplication<Application>().applicationContext
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()

                    val googleSignInClient = GoogleSignIn.getClient(context, gso)

                    // Sign out Google async dengan suspend
                    val googleSignOutSuccess = suspendCancellableCoroutine<Boolean> { cont ->
                        googleSignInClient.signOut()
                            .addOnCompleteListener { task ->
                                cont.resume(task.isSuccessful) {}
                            }
                            .addOnFailureListener {
                                cont.resume(false) {}
                            }
                    }

                    if (!googleSignOutSuccess) {
                        onResult(false, "Gagal logout dari Google")
                        return@launch
                    }

                    // Firebase sign out
                    firebaseAuth.signOut()

                    onResult(true, "Logout berhasil")
                } else {
                    val response = apiService.signOut()
                    if (response.isSucces) {
                        onResult(true, response.message)
                    } else {
                        onResult(false, response.message)
                    }
                }
            } catch (e: Exception) {
                onResult(false, "Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

}
