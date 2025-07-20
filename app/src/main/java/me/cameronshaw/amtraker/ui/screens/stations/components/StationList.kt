package me.cameronshaw.amtraker.ui.screens.stations.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.cameronshaw.amtraker.data.model.Station
import me.cameronshaw.amtraker.ui.common.ListItemSwipeToDeleteBox

@Composable
fun StationList(
    stations: List<Station>,
    modifier: Modifier = Modifier,
    onSwipeToDelete: (Station) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(stations, key = { it.code }) { station ->
            ListItemSwipeToDeleteBox(
                onSwipeToDelete = onSwipeToDelete,
                item = station
            ) {
                StationListItem(
                    station, modifier
                )
            }
        }
    }
}