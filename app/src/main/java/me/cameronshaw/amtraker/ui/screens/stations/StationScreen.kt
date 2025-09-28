package me.cameronshaw.amtraker.ui.screens.stations

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.cameronshaw.amtraker.R
import me.cameronshaw.amtraker.data.model.Station
import me.cameronshaw.amtraker.ui.common.ListPlaceholder
import me.cameronshaw.amtraker.ui.dialogs.AddItemDialog
import me.cameronshaw.amtraker.ui.screens.stations.components.StationList
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme
import java.time.OffsetDateTime

@Composable
fun StationScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: StationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { eventMessage ->
            snackbarHostState.showSnackbar(message = eventMessage)
        }
    }

    StationScreenContent(
        uiState = uiState,
        modifier = modifier,
        validateStationCode = { code ->
            viewModel.isValidStationCode(code)
        },
        onAddStation = { code ->
            viewModel.addStation(code)
        },
        onDeleteStation = { station ->
            viewModel.deleteStation(station)
        },
        onRefresh = {
            viewModel.refreshStations()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationScreenContent(
    uiState: StationUiState,
    modifier: Modifier = Modifier,
    validateStationCode: (String) -> Boolean,
    onAddStation: (String) -> Boolean,
    onDeleteStation: (Station) -> Unit,
    onRefresh: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    if (showAddDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_station_name),
            fieldLabel = stringResource(R.string.add_station_field),
            onDismissRequest = { showAddDialog = false },
            validateInput = validateStationCode,
            onConfirm = onAddStation,
            addFailedMessage = "Failed to add station. Please try again."
        )
    }

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
        } else if (uiState.stations.isEmpty()) {
            ListPlaceholder(
                message = stringResource(R.string.no_stations_tracked),
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        } else {
            StationList(
                stations = uiState.stations,
                modifier = modifier,
                onSwipeToDelete = onDeleteStation
            )
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Station")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StationListPopulatedPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val stations = ('A'..'Z').map {
        Station(
            "$it$it$it", "$it Station", lastUpdated = OffsetDateTime.now()
        )
    }
    val stationUiState = StationUiState(isRefreshing = isLoading, stations = stations)
    val scope = rememberCoroutineScope()
    AmtrakerTheme {
        StationScreenContent(
            stationUiState,
            validateStationCode = { it.isNotEmpty() && it.all(Char::isLetter) },
            onAddStation = { true },
            onDeleteStation = {},
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

@Preview(showBackground = true)
@Composable
fun StationListEmptyPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val stations = emptyList<Station>()
    val stationUiState = StationUiState(isRefreshing = isLoading, stations = stations)
    val scope = rememberCoroutineScope()
    AmtrakerTheme {
        StationScreenContent(
            stationUiState,
            validateStationCode = { it.isNotEmpty() && it.all(Char::isLetter) },
            onAddStation = { true },
            onDeleteStation = {},
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

@Preview(showBackground = true)
@Composable
fun StationListErrorPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val stations = emptyList<Station>()
    val stationUiState = StationUiState(
        isRefreshing = isLoading,
        stations = stations,
        errorMessage = "This is an error message on the stations screen."
    )
    val scope = rememberCoroutineScope()
    AmtrakerTheme {
        StationScreenContent(
            stationUiState,
            validateStationCode = { it.isNotEmpty() && it.all(Char::isLetter) },
            onAddStation = { true },
            onDeleteStation = {},
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