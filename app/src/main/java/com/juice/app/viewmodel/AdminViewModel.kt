package com.juice.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juice.app.JuiceApplication
import com.juice.app.model.Stall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val stalls: List<Stall> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as JuiceApplication
    private val apiService = app.apiService
    val locationService = app.locationService

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadStalls()
    }

    fun loadStalls() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stalls = apiService.getAllStalls()
                _uiState.update { it.copy(stalls = stalls, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun createStall(
        name: String,
        description: String?,
        imageUrl: String?,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, saveSuccess = false) }
            try {
                val stall = Stall(
                    name = name,
                    description = description,
                    imageUrl = imageUrl,
                    latitude = latitude,
                    longitude = longitude
                )
                apiService.createStall(stall)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                loadStalls()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun updateStall(
        id: Int,
        name: String,
        description: String?,
        imageUrl: String?,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, saveSuccess = false) }
            try {
                val stall = Stall(
                    name = name,
                    description = description,
                    imageUrl = imageUrl,
                    latitude = latitude,
                    longitude = longitude
                )
                apiService.updateStall(id, stall)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                loadStalls()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun deleteStall(id: Int) {
        viewModelScope.launch {
            try {
                apiService.deleteStall(id)
                loadStalls()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
