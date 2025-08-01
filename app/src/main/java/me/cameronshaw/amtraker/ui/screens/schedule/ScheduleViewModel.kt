package me.cameronshaw.amtraker.ui.screens.schedule

import me.cameronshaw.amtraker.ui.screens.schedule.components.ScheduleCardDatum

data class ScheduleUiState(
    val isRefreshing: Boolean = false,
    val scheduleData: List<ScheduleCardDatum> = emptyList(),
    val errorMessage: String? = null
)

class ScheduleViewModel {

}