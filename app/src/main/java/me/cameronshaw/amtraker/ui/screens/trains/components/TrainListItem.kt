package me.cameronshaw.amtraker.ui.screens.trains.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.common.ListItem
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme
import java.time.OffsetDateTime

@Composable
fun TrainListItem(
    train: Train,
    modifier: Modifier = Modifier,
) = ListItem(
    modifier = modifier,
    title = "Train ${train.num}",
    subtitle = train.routeName,
    isStale = train.isStale
)

// --- Previews for different component states ---

@Preview(showBackground = true)
@Composable
fun TrainListItemActivePreview() {
    val activeTrain = Train(
        num = "727",
        routeName = "Capitol Corridor",
        lastUpdated = OffsetDateTime.now(),
        stops = emptyList()
    )
    AmtrakerTheme {
        TrainListItem(train = activeTrain)
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListItemInactivePreview() {
    val inactiveTrain = Train(
        num = "727",
        routeName = "",
        lastUpdated = OffsetDateTime.now().minusHours(1),
        stops = emptyList()
    )
    AmtrakerTheme {
        TrainListItem(train = inactiveTrain)
    }
}
