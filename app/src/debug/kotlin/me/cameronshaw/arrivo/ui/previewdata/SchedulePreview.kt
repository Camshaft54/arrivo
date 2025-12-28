package me.cameronshaw.arrivo.ui.previewdata

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cameronshaw.arrivo.data.model.ScheduleDatum
import me.cameronshaw.arrivo.ui.screens.schedule.ScheduleScreenContent
import me.cameronshaw.arrivo.ui.screens.schedule.ScheduleUiState
import me.cameronshaw.arrivo.ui.theme.ArrivoTheme

@Preview(showBackground = true)
@Composable
fun ScheduleListPopulatedPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val scheduleData = List(5) {
        ScheduleDatum(
            train = SampleData.train(it),
            departureStop = SampleData.stop1,
            arrivalStop = SampleData.stop2
        )
    }
    val scheduleUiState = ScheduleUiState(isRefreshing = isLoading, scheduleData = scheduleData)
    val scope = rememberCoroutineScope()
    ArrivoTheme {
        ScheduleScreenContent(
            scheduleUiState,
            onRefresh = {
                scope.launch {
                    isLoading = true
                    delay(1000)
                    isLoading = false
                }
            },
            onNavigateToDetail = {}
        )
    }
}