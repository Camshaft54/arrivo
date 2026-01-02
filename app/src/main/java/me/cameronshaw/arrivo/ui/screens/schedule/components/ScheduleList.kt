package me.cameronshaw.arrivo.ui.screens.schedule.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.arrivo.data.model.ScheduleDatum

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    scheduleCardData: List<ScheduleDatum>,
    onTrainClicked: (trainId: String) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(scheduleCardData, key = { it.train.id }) { datum ->
            ScheduleCard(
                modifier = modifier,
                datum.train,
                datum.departureStop,
                datum.arrivalStop,
                onClick = { onTrainClicked(datum.train.id) }
            )
        }
    }
}

