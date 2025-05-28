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
class currencyViewModel @Inject constructor(private val apiService: SnapCashApiService) :
    ViewModel()
    {
        val currencyList = mutableStateOf(arrayOf<JsonObject>())

        fun getCurrency() {
            viewModelScope.launch {
                try {
                    val response =
                        apiService.getCurrency("Bearer ${SessionManager.idToken}")
                    if (response.isSucces) {
                        currencyList.value = response.data
                    }
                } catch (e: Exception) {
                    Log.e("EXCEPTION", "Exception: ${e.message}", e)
                }

            }
        }
    }


