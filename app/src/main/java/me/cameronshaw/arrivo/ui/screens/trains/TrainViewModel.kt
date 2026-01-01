package me.cameronshaw.arrivo.ui.screens.trains

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.cameronshaw.arrivo.data.model.TrackedTrain
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.repository.TrackedTrainRepository
import me.cameronshaw.arrivo.data.repository.TrainRepository
import java.time.OffsetDateTime
import javax.inject.Inject

data class TrainUiState(
    val isRefreshing: Boolean = false,
    val trains: List<TrackedTrainWithInstance> = emptyList(),
    val errorMessage: String? = null
)

data class TrackedTrainWithInstance(
    val trackedTrain: TrackedTrain,
    val trains: List<Train>
)

/**
 * ViewModel for the Trains screen.
 *
 * Use eventFlow for emitting non-blocking errors like pull to refresh failing.
 *
 * Use errorMessage in the UiState for showing blocking errors like failing to load trains.
 */
@HiltViewModel
class TrainViewModel @Inject constructor(
    private val trackedTrainRepository: TrackedTrainRepository,
    private val trainRepository: TrainRepository
) : ViewModel() {
    private val trainNumRegex = Regex("^[0-1]?[1-9][0-9]{0,2}$")

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(TrainUiState(isRefreshing = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadTrains()
    }

    private fun loadTrains() {
        viewModelScope.launch {
            trackedTrainRepository
                .getTrackedTrains()
                .catch { cause ->
                    Log.e("TrainViewModel", "Failed to load trains.", cause)
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to load trains.",
                            isRefreshing = false
                        )
                    }
                }
                .combine(trainRepository.getTrains()) {
                    trackedTrains, trains ->
                    trackedTrains.map { trackedTrain ->
                        TrackedTrainWithInstance(
                            trackedTrain = trackedTrain,
                            trains = trains.filter { it.num == trackedTrain.num } // TODO only include trains with matching origin date too
                        )
                    }
                }
                .collect { trains ->
                    _uiState.update { it.copy(trains = trains, isRefreshing = false) }
                }
        }
    }

    fun addTrain(num: String, date: OffsetDateTime? = null): Boolean {
        if (!isValidTrainNum(num)) return false
        val newTrain = Train(num)
        viewModelScope.launch {
            try {
                trackedTrainRepository.insertTrackedTrain(TrackedTrain(newTrain.num, date))
                try {
                    trainRepository.refreshAllTrains()
                } catch (_: Exception) {
                    _eventFlow.emit("Failed to fetch information about trains.")
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to add train.") }
            }
        }
        return true
    }

    fun deleteTrain(train: TrackedTrain) {
        viewModelScope.launch {
            try {
                trackedTrainRepository.deleteTrackedTrain(train)
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete train.") }
            }
        }
    }

    fun refreshTrains() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                trainRepository.refreshAllTrains()
            } catch (e: Exception) {
                _eventFlow.emit("Refresh failed. Please try again.")
                Log.d("TrainViewModel", "Train refresh failed.", e)
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    /**
     * Validates the train number. Must be an integer between 1 and 1999.
     */
    fun isValidTrainNum(num: String): Boolean = trainNumRegex.matches(num)
}