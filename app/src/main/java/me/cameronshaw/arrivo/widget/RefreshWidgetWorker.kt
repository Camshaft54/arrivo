package me.cameronshaw.arrivo.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import me.cameronshaw.arrivo.data.repository.ScheduleRepository
import me.cameronshaw.arrivo.data.repository.SettingsRepository
import java.time.OffsetDateTime

@HiltWorker
class RefreshWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduleRepository: ScheduleRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val UNIQUE_WORK_NAME = "RefreshWidgetWorker"
    }

    override suspend fun doWork(): Result {
        scheduleRepository.refreshSchedule()
        val scheduleData = scheduleRepository.getScheduleData().first()
        val appSettings = settingsRepository.appSettingsFlow().first()
        val newWidgetState = WidgetState(OffsetDateTime.now(), appSettings, scheduleData)

        val arrivoWidget = ArrivoWidget()
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(ArrivoWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, arrivoWidget.stateDefinition, glanceId) {
                newWidgetState
            }
        }

        arrivoWidget.updateAll(context)

        return Result.success()
    }
}