package me.cameronshaw.amtraker.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import me.cameronshaw.amtraker.R
import me.cameronshaw.amtraker.data.model.Status
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.theme.Amber
import me.cameronshaw.amtraker.ui.theme.Green
import me.cameronshaw.amtraker.ui.theme.Red
import me.cameronshaw.amtraker.ui.util.determineArrivalStopDescription
import me.cameronshaw.amtraker.ui.util.determineDepartureStopDescription
import me.cameronshaw.amtraker.ui.util.toUiString
import me.cameronshaw.amtraker.widget.theme.GlanceAmtrakerTheme
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AmtrakerWidget : GlanceAppWidget() {
    override val stateDefinition = WidgetStateDefinition(
        serializer = WidgetStateSerializer, fileName = "widget_state.json"
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceAmtrakerTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    fun WidgetContent() {
        val state = currentState<WidgetState>()

        Column(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface)
                .cornerRadius(16.dp).padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                // New Column to group the title and timestamp
                Column {
                    Text(
                        text = "Amtraker Schedule", style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = GlanceTheme.colors.onSurface
                        )
                    )
                    Text(
                        text = "Last updated: ${
                            state.lastUpdated.format(
                                DateTimeFormatter.ofLocalizedTime(
                                    FormatStyle.SHORT
                                )
                            )
                        }", style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
                    )
                }
                Spacer(GlanceModifier.defaultWeight())
                Button(
                    text = "Refresh", onClick = actionRunCallback<RefreshWidgetAction>()
                )
            }

            // Content
            if (state.schedule.isEmpty()) {
                Box(
                    modifier = GlanceModifier.fillMaxSize().defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No schedule to show.",
                        style = TextStyle(color = GlanceTheme.colors.onSurfaceVariant)
                    )
                }
            } else {
                LazyColumn(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    items(state.schedule, { it.train.num.toLong() }) { datum ->
                        Box(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(GlanceTheme.colors.surface)
                        ) {
                            GlanceScheduleCard(
                                // Modifier with padding removed from here
                                train = datum.train,
                                departureStop = datum.departureStop,
                                arrivalStop = datum.arrivalStop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlanceScheduleCard(
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

@SuppressLint("RestrictedApi")
@Composable
private fun GlanceStationInfoColumn(
    stationCode: String, description: String, status: Status, time: OffsetDateTime?
) {
    val statusColor = when (status) {
        Status.EARLY -> ColorProvider(Amber)
        Status.ON_TIME -> ColorProvider(Green)
        Status.LATE -> ColorProvider(Red)
        Status.UNKNOWN -> GlanceTheme.colors.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
            Text(
                text = stationCode, style = TextStyle(
                    fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurfaceVariant
                )
            )
            Spacer(GlanceModifier.size(4.dp))
            Image(
                provider = ImageProvider(R.drawable.ic_circle),
                contentDescription = status.description,
                colorFilter = androidx.glance.ColorFilter.tint(statusColor),
                modifier = GlanceModifier.size(14.dp)
            )
        }
        Text(
            text = description,
            style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
        )
        Text(
            text = time?.toUiString() ?: "--:--", style = TextStyle(
                fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurfaceVariant
            )
        )
    }
}


class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<RefreshWidgetWorker>().build()
        workManager.enqueue(request)
    }
}