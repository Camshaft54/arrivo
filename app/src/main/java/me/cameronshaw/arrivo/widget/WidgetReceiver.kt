package me.cameronshaw.arrivo.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.GlanceStateDefinition
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.cameronshaw.arrivo.data.repository.ScheduleRepository
import me.cameronshaw.arrivo.data.repository.SettingsRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ArrivoWidget()

    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    companion object {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        private var job: Job? = null
    }

    private fun observeData(context: Context) {
        if (job?.isActive == true) return

        job = scope.launch {
            scheduleRepository.getScheduleData().combine(
                settingsRepository.appSettingsFlow()
            ) { scheduleData, appSettings ->
                WidgetState(appSettings, scheduleData)
            }.collect { newWidgetState ->
                val manager = GlanceAppWidgetManager(context)

                manager.getGlanceIds(ArrivoWidget::class.java).forEach { glanceId ->
                    @Suppress("UNCHECKED_CAST")
                    updateAppWidgetState(
                        context,
                        glanceAppWidget.stateDefinition as GlanceStateDefinition<WidgetState>,
                        glanceId
                    ) {
                        newWidgetState
                    }
                }
                glanceAppWidget.updateAll(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        enqueueRefreshWork(context)
        observeData(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        enqueueRefreshWork(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WorkManager.getInstance(context).cancelUniqueWork(RefreshWidgetWorker.UNIQUE_WORK_NAME)
        scope.cancel()
    }

    private fun enqueueRefreshWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<RefreshWidgetWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            RefreshWidgetWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}