package me.cameronshaw.arrivo.ui.screens.schedule

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import me.cameronshaw.arrivo.R
import me.cameronshaw.arrivo.ui.Screen
import me.cameronshaw.arrivo.ui.common.ListPlaceholder
import me.cameronshaw.arrivo.ui.screens.schedule.components.ScheduleList

@Composable
fun ScheduleScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { eventMessage ->
            snackbarHostState.showSnackbar(message = eventMessage)
        }
    }

    ScheduleScreenContent(
        uiState = uiState,
        modifier = modifier,
        onRefresh = {
            viewModel.refreshSchedule()
        },
        onNavigateToDetail = { trainId ->
            navController.navigate("${Screen.ScheduleDetail.route}/$trainId")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenContent(
    uiState: ScheduleUiState,
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    onNavigateToDetail: (trainId: String) -> Unit
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
                onTrainClicked = onNavigateToDetail,
                modifier = modifier
            )
        }
    }
}