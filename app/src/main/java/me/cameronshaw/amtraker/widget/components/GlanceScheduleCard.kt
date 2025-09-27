package me.cameronshaw.amtraker.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.util.determineArrivalStopDescription
import me.cameronshaw.amtraker.ui.util.determineDepartureStopDescription

@Composable
fun GlanceScheduleCard(
    modifier: GlanceModifier = GlanceModifier,
    train: Train,
    departureStop: Train.Stop?,
    arrivalStop: Train.Stop?
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GlanceTheme.colors.surfaceVariant)
            .cornerRadius(12.dp)
    ) {
        Row(
            modifier = GlanceModifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            // Train Info
            Column(
                modifier = GlanceModifier.defaultWeight(), horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = train.num, style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )
                )
                Text(
                    text = train.routeName,
                    style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
                )
            }

            // Departure and Arrival Stops
            if (departureStop != null) {
                GlanceStationInfoColumn(
                    stationCode = departureStop.code,
                    status = departureStop.status,
                    description = departureStop.determineDepartureStopDescription(),
                    time = departureStop.departure ?: departureStop.scheduledDeparture
                )
            }

            Spacer(GlanceModifier.size(16.dp))

            if (arrivalStop != null) {
                GlanceStationInfoColumn(
                    stationCode = arrivalStop.code,
                    status = arrivalStop.status,
                    description = arrivalStop.determineArrivalStopDescription(),
                    time = arrivalStop.arrival ?: arrivalStop.scheduledArrival
                )
            }
        }
    }
}