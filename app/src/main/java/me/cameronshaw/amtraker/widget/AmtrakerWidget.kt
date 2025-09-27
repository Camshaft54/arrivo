package me.cameronshaw.amtraker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import me.cameronshaw.amtraker.widget.components.GlanceScheduleCard
import me.cameronshaw.amtraker.widget.theme.GlanceAmtrakerTheme
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
                        }", style = TextStyle(
                            fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant
                        )
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
                            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp)
                                .background(GlanceTheme.colors.surface)
                        ) {
                            GlanceScheduleCard(
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


class RefreshWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters
    ) {
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<RefreshWidgetWorker>().build()
        workManager.enqueue(request)
    }
}