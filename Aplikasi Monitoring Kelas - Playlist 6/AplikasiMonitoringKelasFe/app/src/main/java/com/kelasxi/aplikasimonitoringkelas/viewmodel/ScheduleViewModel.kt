package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.Schedule
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {
    
    var schedules = mutableStateOf<List<Schedule>>(emptyList())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    
    fun loadSchedules(token: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            try {
                val response = RetrofitClient.apiService.getSchedules("Bearer $token")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        schedules.value = body.data
                        println("DEBUG: Successfully loaded ${body.data.size} schedules")
                    } else {
                        val errorMsg = body?.message ?: "Gagal memuat jadwal"
                        errorMessage.value = errorMsg
                        println("DEBUG: API returned error: $errorMsg")
                    }
                } else {
                    val errorMsg = "Error: ${response.code()} - ${response.message()}"
                    errorMessage.value = errorMsg
                    println("DEBUG: HTTP Error: $errorMsg")
                    // Try to parse error body
                    try {
                        val errorBody = response.errorBody()?.string()
                        println("DEBUG: Error Body: $errorBody")
                    } catch (e: Exception) {
                        println("DEBUG: Could not read error body: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Terjadi kesalahan saat memuat jadwal"
                errorMessage.value = errorMsg
                println("DEBUG: Exception occurred: $errorMsg")
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        errorMessage.value = null
    }
}
