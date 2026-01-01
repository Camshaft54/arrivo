package me.cameronshaw.arrivo.ui.screens.trains.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.arrivo.data.model.TrackedTrain
import me.cameronshaw.arrivo.ui.common.ListItemSwipeToDeleteBox
import me.cameronshaw.arrivo.ui.screens.trains.TrackedTrainWithInstance

@Composable
fun TrainList(
    trains: List<TrackedTrainWithInstance>, modifier: Modifier = Modifier, onSwipeToDelete: (TrackedTrain) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(trains, key = { it.trackedTrain.num }) { train ->
            ListItemSwipeToDeleteBox(
                onSwipeToDelete = onSwipeToDelete, item = train.trackedTrain
            ) {
                TrainListItem(train)
            }
        }
    }
}