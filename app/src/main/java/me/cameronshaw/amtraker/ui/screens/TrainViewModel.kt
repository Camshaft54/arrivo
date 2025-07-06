package me.cameronshaw.amtraker.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.repository.TrainRepository
import javax.inject.Inject

data class TrainUiState(
    val isLoading: Boolean = false,
    val trains: List<Train> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class TrainViewModel @Inject constructor(
    private val trainRepository: TrainRepository
) : ViewModel() {
    private val trainNumRegex = Regex("^[1-9][0-9]{0,2}\$")

    val uiState: StateFlow<TrainUiState> = trainRepository
        .getTrackedTrains()
        .map { trains ->
            TrainUiState(trains = trains)
        }
        .catch {
            emit(TrainUiState(errorMessage = "Failed to load trains."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrainUiState(isLoading = true)
        )

    fun addTrain(num: String): Boolean {
        if (!isValidTrainNum(num)) return false
        val newTrain = Train(num)
        viewModelScope.launch {
            trainRepository.addTrain(newTrain)
            trainRepository.refreshTrain(num)
        }
        return true
    }

    fun deleteTrain(train: Train) {
        viewModelScope.launch {
            trainRepository.deleteTrain(train)
        }
    }

    fun refreshTrains() {
        viewModelScope.launch {
            trainRepository.refreshAllTrains()
        }
    }

    /**
     * Validates the train number. Must be an integer between 1 and 999.
     */
    private fun isValidTrainNum(num: String): Boolean = trainNumRegex.matches(num)
}