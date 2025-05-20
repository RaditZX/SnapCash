package com.example.snapcash.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcash.data.SessionManager
import com.example.snapcash.data.SnapCashApiService
import com.example.snapcash.data.DashboardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val apiService: SnapCashApiService
) : ViewModel() {

    val isLoading = mutableStateOf(false)
    val dashboardData = mutableStateOf<DashboardData?>(null)

    fun setDashboardData(data: DashboardData) {
        dashboardData.value = data
    }

    fun getDashboardAnalytics(
        jenis: String = "Pemasukan",
        filter: String = "tahun",
        tahun: Int,
        bulan: Int? = null,
        hari: Int? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getDashboardAnalytics(
                    token = "Bearer ${SessionManager.idToken}",
                    jenis = jenis,
                    filter = filter,
                    tahun = tahun,
                    bulan = bulan,
                    hari = hari
                )
                if (response.isSucces && response.data != null) {
                    setDashboardData(response.data)
                } else {
                    Log.e("DashboardViewModel", "Empty or invalid response")
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error: ${e.message}", e)
            } finally {
                isLoading.value = false
            }
        }
    }
}
