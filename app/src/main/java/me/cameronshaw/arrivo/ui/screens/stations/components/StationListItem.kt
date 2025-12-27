package me.cameronshaw.arrivo.ui.screens.stations.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.cameronshaw.arrivo.data.model.Station
import me.cameronshaw.arrivo.ui.common.ListItem
import me.cameronshaw.arrivo.ui.theme.ArrivoTheme
import java.time.OffsetDateTime

@Composable
fun StationListItem(
    station: Station,
    modifier: Modifier = Modifier
) = ListItem(
    modifier = modifier,
    title = station.code,
    subtitle = station.name,
    isStale = station.isStale
)

// --- Previews for different component states ---

@Preview(showBackground = true)
@Composable
fun StationListItemActivePreview() {
    val activeStation = Station(
        code = "GAC",
        name = "Santa Clara - Great America",
        lastUpdated = OffsetDateTime.now()
    )
    ArrivoTheme {
        StationListItem(station = activeStation)
    }
}

@Preview(showBackground = true)
@Composable
fun StationListItemInactivePreview() {
    val inactiveStation = Station(
        code = "GAC",
        name = "",
        lastUpdated = OffsetDateTime.now().minusHours(1)
    )
    ArrivoTheme {
        StationListItem(station = inactiveStation)
    }
}
