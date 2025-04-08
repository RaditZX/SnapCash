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
import javax.inject.Inject

@HiltViewModel
class PemasukanViewModel @Inject constructor(private val apiService:SnapCashApiService) : ViewModel()  {
    val pemasukanData = mutableStateOf(arrayOf<JsonObject>())

    fun setPemasukanData(data: Array<JsonObject>) {
        pemasukanData.value = data
    }

    val isLoading = mutableStateOf(true)

    fun getPemasukanUser (){
        viewModelScope.launch{
            isLoading.value = true
            try {
                val response = apiService.getPemasukanUser("Bearer ${SessionManager.idToken}")

                if (response.isSucces){
                    setPemasukanData(response.data)
                }
            }catch (e: Exception){
                Log.e("EXCEPTION", "Exception: ${e.message}", e)
            }finally {
                isLoading.value = false
            }
        }
    }
}