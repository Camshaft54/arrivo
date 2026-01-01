package me.cameronshaw.arrivo.ui.screens.trains.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.cameronshaw.arrivo.data.model.TrackedTrain

import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.ui.common.ListItem
import me.cameronshaw.arrivo.ui.screens.trains.TrackedTrainWithInstance
import me.cameronshaw.arrivo.ui.theme.ArrivoTheme
import java.time.OffsetDateTime

@Composable
fun TrainListItem(
    train: TrackedTrainWithInstance,
    modifier: Modifier = Modifier,
) = ListItem(
    modifier = modifier,
    title = "Train ${train.trackedTrain.num}",
    subtitle = train.trains.firstOrNull()?.routeName ?: "",
    isStale = train.trains.firstOrNull()?.isStale ?: true
)

// --- Previews for different component states ---

@Preview(showBackground = true)
@Composable
fun TrainListItemActivePreview() {
    val activeTrain = Train(
        num = "727",
        originDate = null,
        routeName = "Capitol Corridor",
        provider = "Amtrak",
        velocity = 30.0,
        lastUpdated = OffsetDateTime.now(),
        stops = emptyList()
    )
    ArrivoTheme {
        TrainListItem(
            train = TrackedTrainWithInstance(
                TrackedTrain("727", null),
                listOf(activeTrain)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListItemInactivePreview() {
    val inactiveTrain = Train(
        num = "727",
        originDate = null,
        routeName = "",
        provider = "Amtrak",
        velocity = 0.0,
        lastUpdated = OffsetDateTime.now().minusHours(1),
        stops = emptyList()
    )
    ArrivoTheme {
        TrainListItem(
            train = TrackedTrainWithInstance(
                TrackedTrain("727", null),
                listOf(inactiveTrain)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListItemMissingInstancePreview() {
    ArrivoTheme {
        TrainListItem(
            train = TrackedTrainWithInstance(
                TrackedTrain("727", null),
                emptyList()
            )
        )
    }
}
