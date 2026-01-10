package me.cameronshaw.arrivo.widget.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import me.cameronshaw.arrivo.TRAIN_ID_ARG
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.ui.Screen
import me.cameronshaw.arrivo.ui.util.determineArrivalStopDescription
import me.cameronshaw.arrivo.ui.util.determineDepartureStopDescription

val TrainIdKey = ActionParameters.Key<String>(TRAIN_ID_ARG)

@Composable
fun GlanceScheduleCard(
    modifier: GlanceModifier = GlanceModifier,
    train: Train,
    departureStop: Train.Stop?,
    arrivalStop: Train.Stop?
) {
    val route = "${Screen.ScheduleDetail.route}/${train.id}"
    val deepLinkUri  = "arrivo://$route".toUri()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .cornerRadius(12.dp)
            .clickable(
                actionStartActivity(
                    Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                        `package` = "me.cameronshaw.arrivo"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    },
                    actionParametersOf(
                        TrainIdKey to train.id
                    )
                )
            )
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

            if (departureStop != null && departureStop == arrivalStop) { // 1 stop
                GlanceStationInfoColumn(
                    modifier = GlanceModifier.width(85.dp),
                    stationCode = arrivalStop.code,
                    status = arrivalStop.status,
                    description = arrivalStop.determineArrivalStopDescription(),
                    time = arrivalStop.arrival ?: arrivalStop.scheduledArrival
                )
                Spacer(GlanceModifier.size(16.dp))
                GlanceStationInfoColumn(
                    modifier = GlanceModifier.width(75.dp),
                    stationCode = departureStop.code,
                    status = departureStop.status,
                    description = departureStop.determineDepartureStopDescription(),
                    time = departureStop.departure ?: departureStop.scheduledDeparture
                )
            } else if (departureStop != null && arrivalStop != null) { // 2 stops
                GlanceStationInfoColumn(
                    modifier = GlanceModifier.width(85.dp),
                    stationCode = departureStop.code,
                    status = departureStop.status,
                    description = departureStop.determineDepartureStopDescription(),
                    time = departureStop.departure ?: departureStop.scheduledDeparture
                )
                Spacer(GlanceModifier.size(16.dp))
                GlanceStationInfoColumn(
                    modifier = GlanceModifier.width(85.dp),
                    stationCode = arrivalStop.code,
                    status = arrivalStop.status,
                    description = arrivalStop.determineArrivalStopDescription(),
                    time = arrivalStop.arrival ?: arrivalStop.scheduledArrival
                )
            }
        }
    }
}