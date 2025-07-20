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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.cameronshaw.amtraker.data.model.Station
import me.cameronshaw.amtraker.ui.common.AddItemDialog
import me.cameronshaw.amtraker.ui.common.ListPlaceholder
import me.cameronshaw.amtraker.ui.screens.stations.components.StationList

@Composable
fun StationScreen(
    modifier: Modifier = Modifier,
    viewModel: StationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            title = "Add Station",
            fieldLabel = "Train Number",
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
                message = "No stations are being tracked. Tap the '+' button to add ...",
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        } else {
            StationList(
                stations = uiState.stations,
                modifier = modifier,
                onSwipeToDelete = {
                    onDeleteStation(it)
                }
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