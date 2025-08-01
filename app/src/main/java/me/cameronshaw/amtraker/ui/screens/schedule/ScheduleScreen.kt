package me.cameronshaw.amtraker.ui.screens.schedule

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cameronshaw.amtraker.R
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.common.ListPlaceholder
import me.cameronshaw.amtraker.ui.screens.schedule.components.ScheduleCardDatum
import me.cameronshaw.amtraker.ui.screens.schedule.components.ScheduleList
import me.cameronshaw.amtraker.ui.screens.trains.TrainScreenContent
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenContent(
    uiState: ScheduleUiState,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        state = pullToRefreshState,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        if (!uiState.errorMessage.isNullOrEmpty()) {
            ListPlaceholder(
                message = uiState.errorMessage,
                isError = true,
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        } else if (uiState.scheduleData.isEmpty()) {
            ListPlaceholder(
                message = stringResource(R.string.schedule_empty_message),
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        } else {
            ScheduleList(
                scheduleCardData = uiState.scheduleData,
                modifier = modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleListPopulatedPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val scheduleData = List(5) {
        val stop1 = Train.Stop(
            code = "OKJ",
            name = "Oakland - Jack London",
            arrival = OffsetDateTime.now(),
            departure = OffsetDateTime.now().plusMinutes(2)
        )
        val stop2 = Train.Stop(
            code = "GAC",
            name = "Santa Clara - Great America",
            arrival = OffsetDateTime.now().plusHours(1),
            departure = OffsetDateTime.now().plusHours(1).plusMinutes(2)
        )

        ScheduleCardDatum(
            train = Train(
                "$it", "Route $it", listOf(stop1, stop2), lastUpdated = OffsetDateTime.now()
            ),
            departureStop = stop1,
            arrivalStop = stop2
        )
    }
    val scheduleUiState = ScheduleUiState(isRefreshing = isLoading, scheduleData = scheduleData)
    val scope = rememberCoroutineScope()
    AmtrakerTheme {
        ScheduleScreenContent(
            scheduleUiState,
            onRefresh = {
                scope.launch {
                    isLoading = true
                    delay(1000)
                    isLoading = false
                }
            }
        )
    }
}