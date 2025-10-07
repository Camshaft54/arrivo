package me.cameronshaw.amtraker.ui.screens.scheduledetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.util.determineArrivalStopFullDescription
import me.cameronshaw.amtraker.ui.util.determineDepartureStopFullDescription
import java.time.OffsetDateTime

/**
 * A composable that displays a single stop in a train's schedule.
 *
 * @param stop The stop to display.
 * @param modifier The modifier to be applied to this component.
 * @param isFirstStop True if this is the first stop in the list, which omits the top line.
 * @param isLastStop True if this is the last stop in the list, which omits the bottom line.
 */
@Composable
fun ScheduleDetailStopItem(
    stop: Train.Stop,
    modifier: Modifier = Modifier,
    isFirstStop: Boolean = false,
    isLastStop: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        val columnVerticalPadding = 8.dp
        val stationNameStyle = MaterialTheme.typography.bodyLarge
        val halfStopNameHeight = with(LocalDensity.current) { (stationNameStyle.lineHeight.toDp() / 2) }
        val circleYOffset = columnVerticalPadding + halfStopNameHeight
        // Left column for the timeline graphic
        TimelineNode(
            isFirstStop = isFirstStop,
            isLastStop = isLastStop,
            hasArrived = stop.arrivedAt,
            circleYTarget = circleYOffset,
            modifier = Modifier.fillMaxHeight()
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Right column for the station details
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = columnVerticalPadding)
        ) {
            Text(
                text = "${stop.name} (${stop.code})",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stop.determineArrivalStopFullDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stop.determineDepartureStopFullDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Draws the timeline graphic (circle and connecting lines).
 */
@Composable
private fun TimelineNode(
    isFirstStop: Boolean,
    isLastStop: Boolean,
    hasArrived: Boolean,
    circleYTarget: Dp,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
) {
    Canvas(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .width(20.dp)
    ) {
        val circleRadius = 8.dp.toPx()
        val strokeWidth = 3.dp.toPx()

        // Center of the canvas
        val circleCenterY = circleYTarget.toPx().coerceIn(circleRadius, size.height - circleRadius)
        val circleCenter = Offset(center.x, circleCenterY)

        // Draw the top line if it's not the first stop
        if (!isFirstStop) {
            drawTopLine(circleCenter, circleRadius, strokeWidth, lineColor)
        }

        // Draw the bottom line if it's not the last stop
        if (!isLastStop) {
            drawBottomLine(circleCenter, circleRadius, strokeWidth, lineColor)
        }

        // Draw the circle in line with the stop name
        if (hasArrived) {
            drawCircle(
                color = lineColor, radius = circleRadius, center = circleCenter
            )
        } else {
            drawCircle(
                color = lineColor,
                radius = circleRadius,
                center = circleCenter,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

private fun DrawScope.drawTopLine(center: Offset, radius: Float, width: Float, color: Color) {
    drawLine(
        color = color,
        start = Offset(center.x, 0f),
        end = Offset(center.x, center.y - radius),
        strokeWidth = width
    )
}

private fun DrawScope.drawBottomLine(center: Offset, radius: Float, width: Float, color: Color) {
    drawLine(
        color = color,
        start = Offset(center.x, center.y + radius),
        end = Offset(center.x, size.height),
        strokeWidth = width
    )
}

// You can use this preview to see how the component looks in isolation.
@Preview(showBackground = true)
@Composable
private fun TrainStopItemPreview() {
    val stop1 = Train.Stop(
        code = "OKJ",
        name = "Oakland - Jack London",
        arrival = OffsetDateTime.now(),
        departure = OffsetDateTime.now().plusMinutes(2),
        scheduledArrival = OffsetDateTime.now(),
        scheduledDeparture = OffsetDateTime.now().plusMinutes(2)
    )
    val stop2 = Train.Stop(
        code = "FMT",
        name = "Fremont",
        arrival = OffsetDateTime.now().plusMinutes(30),
        departure = OffsetDateTime.now().plusMinutes(30).plusMinutes(2),
        scheduledArrival = OffsetDateTime.now().plusMinutes(30),
        scheduledDeparture = OffsetDateTime.now().plusMinutes(30).plusMinutes(2)
    )
    val stop3 = Train.Stop(
        code = "GAC",
        name = "Santa Clara - Great America",
        arrival = null,
        departure = OffsetDateTime.now().plusHours(1).plusMinutes(2),
        scheduledArrival = OffsetDateTime.now().plusHours(1),
        scheduledDeparture = OffsetDateTime.now().plusHours(1).plusMinutes(2)
    )

    MaterialTheme { // Replace with your app's theme
        Surface {
            Column {
                ScheduleDetailStopItem(
                    stop = stop1, isFirstStop = true
                )
                ScheduleDetailStopItem(
                    stop = stop2,
                )
                ScheduleDetailStopItem(
                    stop = stop3, isLastStop = true
                )
            }
        }
    }
}