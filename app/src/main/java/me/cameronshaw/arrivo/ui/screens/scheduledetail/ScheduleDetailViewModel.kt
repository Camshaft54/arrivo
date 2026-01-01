package me.cameronshaw.arrivo.ui.screens.scheduledetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
import me.cameronshaw.arrivo.TRAIN_ID_ARG
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.repository.TrainRepository
import javax.inject.Inject

data class ScheduleDetailUiState(
    val train: Train? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ScheduleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val trainRepository: TrainRepository
) : ViewModel() {
    private val trainId: String = checkNotNull(savedStateHandle[TRAIN_ID_ARG])
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val _uiState = MutableStateFlow(ScheduleDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTrain()
    }

    private fun loadTrain() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            trainRepository
                .getTrain(trainId)
                .catch { cause ->
                    Log.e("TrainViewModel", "Failed to load trains.", cause)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load trains."
                        )
                    }
                }
                .collect { train ->
                    _uiState.update {
                        it.copy(
                            train = train,
                            errorMessage = null,
                            isLoading = false
                        )
                    }
                }
        }
    }


}