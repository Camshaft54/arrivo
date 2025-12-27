package me.cameronshaw.arrivo.ui.screens.stations

import android.util.Log
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.cameronshaw.arrivo.data.model.Station
import me.cameronshaw.arrivo.data.repository.StationRepository
import javax.inject.Inject

data class StationUiState(
    val isRefreshing: Boolean = false,
    val stations: List<Station> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class StationViewModel @Inject constructor(
    private val stationRepository: StationRepository
) : ViewModel() {
    private val stationCodeRegex = Regex("^[a-zA-Z]{3}\$")

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(StationUiState(isRefreshing = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            stationRepository
                .getStations()
                .catch { cause ->
                    Log.e("StationViewModel", "Failed to load stations", cause)
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to load stations.",
                            isRefreshing = false
                        )
                    }
                }.collect { stations ->
                    _uiState.update { it.copy(stations = stations, isRefreshing = false) }
                }
        }
    }

    fun addStation(code: String): Boolean {
        if (!isValidStationCode(code)) return false
        val normalizedCode = code.toUpperCase(Locale.current)
        val newStation = Station(normalizedCode)
        viewModelScope.launch {
            try {
                stationRepository.addStation(newStation)
                try {
                    stationRepository.refreshStation(normalizedCode)
                } catch (_: Exception) {
                    _eventFlow.emit("Failed to get station information. Please try again.")
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add station.") }
            }
        }
        return true
    }

    fun deleteStation(station: Station) {
        viewModelScope.launch {
            try {
                stationRepository.deleteStation(station)
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete station.") }
            }
        }
    }

    fun refreshStations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                stationRepository.refreshAllStations()
            } catch (e: Exception) {
                _eventFlow.emit("Refresh failed. Please try again.")
                Log.d("StationViewModel", "Station refresh failed.", e)
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    /**
     * Validate the station code. Must be a 3 character string.
     */
    fun isValidStationCode(code: String): Boolean = stationCodeRegex.matches(code)
}