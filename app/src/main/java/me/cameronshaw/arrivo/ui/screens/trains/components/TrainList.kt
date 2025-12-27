package me.cameronshaw.arrivo.ui.screens.trains.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.ui.common.ListItemSwipeToDeleteBox

@Composable
fun TrainList(
    trains: List<Train>, modifier: Modifier = Modifier, onSwipeToDelete: (Train) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(trains, key = { it.num }) { train ->
            ListItemSwipeToDeleteBox(
                onSwipeToDelete = onSwipeToDelete, item = train
            ) {
                TrainListItem(train)
            }
        }
    }
}