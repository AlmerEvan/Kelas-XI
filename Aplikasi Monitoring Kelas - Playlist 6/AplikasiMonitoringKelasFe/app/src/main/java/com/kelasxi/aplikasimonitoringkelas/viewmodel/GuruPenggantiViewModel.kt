package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepositoryNew
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GuruPenggantiUiState {
    object Idle : GuruPenggantiUiState()
    object Loading : GuruPenggantiUiState()
    data class Success(val data: List<GuruPengganti>) : GuruPenggantiUiState()
    data class Error(val message: String) : GuruPenggantiUiState()
}

sealed class GuruPenggantiReportUiState {
    object Idle : GuruPenggantiReportUiState()
    object Loading : GuruPenggantiReportUiState()
    data class Success(val data: List<ReplacementTeacherReportItem>) : GuruPenggantiReportUiState()
    data class Error(val message: String) : GuruPenggantiReportUiState()
}

sealed class ConfirmationUiState {
    object Idle : ConfirmationUiState()
    object Loading : ConfirmationUiState()
    object Success : ConfirmationUiState()
    data class Error(val message: String) : ConfirmationUiState()
    data class AlreadyConfirmed(val message: String) : ConfirmationUiState()
}

class GuruPenggantiViewModel : ViewModel() {
    private val repository = AppRepositoryNew(RetrofitClient.apiService)
    
    // Guru Pengganti List State
    private val _guruPenggantiListState = MutableStateFlow<GuruPenggantiUiState>(GuruPenggantiUiState.Idle)
    val guruPenggantiListState: StateFlow<GuruPenggantiUiState> = _guruPenggantiListState
    
    // Student Confirmation State
    private val _confirmationState = MutableStateFlow<ConfirmationUiState>(ConfirmationUiState.Idle)
    val confirmationState: StateFlow<ConfirmationUiState> = _confirmationState
    
    // Report State
    private val _reportState = MutableStateFlow<GuruPenggantiReportUiState>(GuruPenggantiReportUiState.Idle)
    val reportState: StateFlow<GuruPenggantiReportUiState> = _reportState
    
    // Specific Guru Pengganti Confirmations State
    private val _specificConfirmationsState = MutableStateFlow<GuruPenggantiUiState>(GuruPenggantiUiState.Idle)
    val specificConfirmationsState: StateFlow<GuruPenggantiUiState> = _specificConfirmationsState
    
    /**
     * Fetch guru pengganti list for student
     */
    fun getGuruPengganti(token: String, tanggal: String? = null, kelas: String? = null) {
        viewModelScope.launch {
            _guruPenggantiListState.value = GuruPenggantiUiState.Loading
            repository.getGuruPengganti(token, tanggal, kelas)
                .onSuccess { response ->
                    _guruPenggantiListState.value = GuruPenggantiUiState.Success(response.data)
                }
                .onFailure { error ->
                    _guruPenggantiListState.value = GuruPenggantiUiState.Error(
                        error.message ?: "Terjadi kesalahan saat mengambil data guru pengganti"
                    )
                }
        }
    }
    
    /**
     * Confirm replacement teacher attendance by student
     */
    fun confirmReplacementTeacher(token: String, guruPenggantiId: Int, siswaId: Int) {
        viewModelScope.launch {
            _confirmationState.value = ConfirmationUiState.Loading
            repository.confirmReplacementTeacher(token, guruPenggantiId, siswaId)
                .onSuccess { confirmation ->
                    _confirmationState.value = ConfirmationUiState.Success
                }
                .onFailure { error ->
                    val errorMessage = error.message ?: "Terjadi kesalahan saat mengkonfirmasi"
                    if (errorMessage.contains("409") || errorMessage.contains("Anda sudah mengkonfirmasi")) {
                        _confirmationState.value = ConfirmationUiState.AlreadyConfirmed(
                            "Anda sudah mengkonfirmasi guru pengganti ini"
                        )
                    } else {
                        _confirmationState.value = ConfirmationUiState.Error(errorMessage)
                    }
                }
        }
    }
    
    /**
     * Get replacement teacher confirmation report (for Kurikulum)
     */
    fun getReplacementTeacherReport(
        token: String,
        tanggal: String? = null,
        kelas: String? = null,
        search: String? = null
    ) {
        viewModelScope.launch {
            _reportState.value = GuruPenggantiReportUiState.Loading
            repository.getReplacementTeacherReport(token, tanggal, kelas, search)
                .onSuccess { response ->
                    _reportState.value = GuruPenggantiReportUiState.Success(response.data)
                }
                .onFailure { error ->
                    _reportState.value = GuruPenggantiReportUiState.Error(
                        error.message ?: "Terjadi kesalahan saat mengambil laporan"
                    )
                }
        }
    }
    
    /**
     * Get confirmations for specific guru pengganti
     */
    fun getGuruPenggantiConfirmations(token: String, guruPenggantiId: Int) {
        viewModelScope.launch {
            _specificConfirmationsState.value = GuruPenggantiUiState.Loading
            repository.getGuruPenggantiConfirmations(token, guruPenggantiId)
                .onSuccess { response ->
                    // Just mark as success without storing confirmation details in list state
                    _specificConfirmationsState.value = GuruPenggantiUiState.Success(emptyList())
                }
                .onFailure { error ->
                    _specificConfirmationsState.value = GuruPenggantiUiState.Error(
                        error.message ?: "Terjadi kesalahan saat mengambil konfirmasi"
                    )
                }
        }
    }
    
    /**
     * Reset confirmation state
     */
    fun resetConfirmationState() {
        _confirmationState.value = ConfirmationUiState.Idle
    }
    
    /**
     * Reset all states
     */
    fun resetAllStates() {
        _guruPenggantiListState.value = GuruPenggantiUiState.Idle
        _confirmationState.value = ConfirmationUiState.Idle
        _reportState.value = GuruPenggantiReportUiState.Idle
        _specificConfirmationsState.value = GuruPenggantiUiState.Idle
    }
}
