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

data class MapUiState(
    val userLocation: LatLng? = null,
    val stalls: List<Stall> = emptyList(),
    val selectedStall: Stall? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as JuiceApplication
    private val apiService = app.apiService
    private val locationService = app.locationService

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        fetchLocation()
    }

    fun fetchLocation() {
        viewModelScope.launch {
            val location = locationService.getCurrentLocation()
            _uiState.update { it.copy(userLocation = location) }
            if (location != null) {
                fetchNearbyStalls(location.latitude, location.longitude)
            }
        }
    }

    fun fetchNearbyStalls(lat: Double, lng: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stalls = apiService.getNearbyStalls(lat, lng)
                _uiState.update { it.copy(stalls = stalls, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun selectStall(stall: Stall?) {
        _uiState.update { it.copy(selectedStall = stall) }
    }

    fun refresh() {
        val location = _uiState.value.userLocation
        if (location != null) {
            fetchNearbyStalls(location.latitude, location.longitude)
        } else {
            fetchLocation()
        }
    }
}
