package me.cameronshaw.amtraker.widget

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
import me.cameronshaw.amtraker.data.repository.ScheduleRepository
import me.cameronshaw.amtraker.data.repository.SettingsRepository
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

        val amtrakerWidget = AmtrakerWidget()
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(AmtrakerWidget::class.java)
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, amtrakerWidget.stateDefinition, glanceId) {
                newWidgetState
            }
        }

        amtrakerWidget.updateAll(context)

        return Result.success()
    }
}