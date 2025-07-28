import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.common.ListPlaceholder
import me.cameronshaw.amtraker.ui.util.determineArrivalStopDescription
import me.cameronshaw.amtraker.ui.util.determineDepartureStopDescription
import me.cameronshaw.amtraker.ui.util.toUiString
import java.time.OffsetDateTime

// If only one stop is added along the route, make it the departure and arrival stop.
@Composable
fun ScheduleCard( // TODO: fix stop status by adding scheduled arrival/departure or adding a status field
    modifier: Modifier = Modifier,
    train: Train,
    departureStop: Train.Stop?,
    arrivalStop: Train.Stop?
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1.2f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = train.num,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = train.routeName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (departureStop != null) {
                StationInfoColumn(
                    modifier = Modifier.weight(1f),
                    stationCode = departureStop.code,
                    description = departureStop.determineDepartureStopDescription(),
                    time = departureStop.departure
                )
            }

            if (arrivalStop != null) {
                StationInfoColumn(
                    modifier = Modifier.weight(1f),
                    stationCode = arrivalStop.code,
                    description = arrivalStop.determineArrivalStopDescription(),
                    time = arrivalStop.arrival
                )
            }

            if (departureStop == null && arrivalStop == null) {
                Text(
                    text = "No stations added on this route.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
private fun StationInfoColumn(
    modifier: Modifier = Modifier,
    stationCode: String,
    description: String,
    time: OffsetDateTime?
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stationCode,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = "Inactive",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = time?.toUiString() ?: "--:--",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ScheduleCardPreview() {
    val departureStop = Train.Stop(
        code = "OKJ",
        name = "Oakland - Jack London",
        arrival = OffsetDateTime.now(),
        departure = OffsetDateTime.now().plusMinutes(2)
    )
    val arrivalStop = Train.Stop(
        code = "GAC",
        name = "Santa Clara - Great America",
        arrival = OffsetDateTime.now().plusHours(1),
        departure = OffsetDateTime.now().plusHours(1).plusMinutes(2)
    )

    MaterialTheme {
        ScheduleCard(
            train = Train(
                num = "546",
                routeName = "Capitol Corridor",
                stops = listOf(
                    departureStop,
                    arrivalStop
                ),
                lastUpdated = OffsetDateTime.now()
            ),
            departureStop = departureStop,
            arrivalStop = arrivalStop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleCardMissingStopsPreview() {
    MaterialTheme {
        ScheduleCard(
            train = Train(
                num = "546",
                routeName = "Capitol Corridor",
                stops = listOf(),
                lastUpdated = OffsetDateTime.now()
            ),
            departureStop = null,
            arrivalStop = null
        )
    }
}