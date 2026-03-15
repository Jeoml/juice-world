package com.juice.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juice.app.JuiceApplication
import com.juice.app.model.LatLng
import com.juice.app.model.Stall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StallListUiState(
    val stalls: List<Stall> = emptyList(),
    val userLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StallListViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as JuiceApplication
    private val apiService = app.apiService
    private val locationService = app.locationService

    private val _uiState = MutableStateFlow(StallListUiState())
    val uiState: StateFlow<StallListUiState> = _uiState.asStateFlow()

    init {
        loadStalls()
    }

    fun loadStalls() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val location = locationService.getCurrentLocation()
                _uiState.update { it.copy(userLocation = location) }

                val stalls = if (location != null) {
                    apiService.getNearbyStalls(location.latitude, location.longitude)
                } else {
                    apiService.getAllStalls()
                }
                _uiState.update { it.copy(stalls = stalls, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
