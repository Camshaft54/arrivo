package me.cameronshaw.amtraker.ui.screens.schedule.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.amtraker.data.model.Train

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    scheduleCardData: List<ScheduleCardDatum>
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(scheduleCardData, key = { it.train.num }) { datum ->
            ScheduleCardDatum(
                datum.train,
                datum.departureStop,
                datum.arrivalStop
            )
        }
    }
}

data class ScheduleCardDatum(
    val train: Train,
    val departureStop: Train.Stop?,
    val arrivalStop: Train.Stop?
)