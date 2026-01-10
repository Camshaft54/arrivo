package me.cameronshaw.arrivo.ui.screens.schedule

import android.util.Log
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
import me.cameronshaw.arrivo.data.model.ScheduleDatum
import me.cameronshaw.arrivo.data.repository.ScheduleRepository
import me.cameronshaw.arrivo.data.repository.SettingsRepository
import me.cameronshaw.arrivo.data.util.isTimeStale
import javax.inject.Inject

data class ScheduleUiState(
    val isRefreshing: Boolean = false,
    val scheduleData: List<ScheduleDatum> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSchedule()
        checkStaleness()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            scheduleRepository
                .getScheduleData()
                .catch { cause ->
                    Log.e("ScheduleViewModel", "Failed to load schedule", cause)
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to load schedule.",
                            isRefreshing = false
                        )
                    }
                }.collect { scheduleData ->
                    _uiState.update { it.copy(scheduleData = scheduleData, isRefreshing = false) }
                }
        }
    }

    fun checkStaleness() {
        viewModelScope.launch {
            settingsRepository
                .appSettingsFlow().collect {
                    if (it.trainsLastUpdated.isTimeStale()) {
                        _eventFlow.emit("Schedule data is stale. Refreshing...")
                        refreshSchedule()
                    }
                }
        }
    }

    fun refreshSchedule() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                scheduleRepository.refreshSchedule()
            } catch (e: Exception) {
                _eventFlow.emit("Refresh failed. Please try again.")
                Log.d("ScheduleViewModel", "Schedule refresh failed.", e)
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }
}