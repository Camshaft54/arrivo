package me.cameronshaw.amtraker.ui.screens.schedule.components

import ScheduleCard
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.amtraker.data.model.ScheduleDatum

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    scheduleCardData: List<ScheduleDatum>,
    onTrainClicked: (trainId: String) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(scheduleCardData, key = { it.train.num }) { datum ->
            ScheduleCard(
                modifier = modifier,
                datum.train,
                datum.departureStop,
                datum.arrivalStop,
                onClick = { onTrainClicked(datum.train.num) }
            )
        }
    }
}

