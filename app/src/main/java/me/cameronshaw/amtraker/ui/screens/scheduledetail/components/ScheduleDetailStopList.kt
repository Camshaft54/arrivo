package me.cameronshaw.amtraker.ui.screens.scheduledetail.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.amtraker.data.model.Train

@Composable
fun ScheduleDetailStopList(
    modifier: Modifier = Modifier,
    stops: List<Train.Stop>
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(stops, key = { it.code }) { stop ->
            ScheduleDetailStopItem(
                stop = stop,
                modifier = modifier,
                isFirstStop = stops.indexOf(stop) == 0,
                isLastStop = stops.indexOf(stop) == stops.lastIndex
            )
        }
    }
}