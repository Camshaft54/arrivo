package me.cameronshaw.amtraker.ui.screens.trains

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.cameronshaw.amtraker.R
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.dialogs.AddItemDialog
import me.cameronshaw.amtraker.ui.common.ListPlaceholder
import me.cameronshaw.amtraker.ui.screens.trains.components.TrainList
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme
import java.time.OffsetDateTime

@Composable
fun TrainScreen(
    modifier: Modifier = Modifier,
    viewModel: TrainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TrainScreenContent(
        uiState = uiState,
        modifier = modifier,
        validateTrainNum = { num ->
            viewModel.isValidTrainNum(num)
        },
        onAddTrain = { num ->
            viewModel.addTrain(num)
        },
        onDeleteTrain = { train ->
            viewModel.deleteTrain(train)
        },
        onRefresh = {
            viewModel.refreshTrains()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainScreenContent(
    uiState: TrainUiState,
    modifier: Modifier = Modifier,
    validateTrainNum: (String) -> Boolean,
    onAddTrain: (String) -> Boolean,
    onDeleteTrain: (Train) -> Unit,
    onRefresh: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    if (showAddDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_train_title),
            fieldLabel = stringResource(R.string.add_train_field),
            onDismissRequest = { showAddDialog = false },
            validateInput = validateTrainNum,
            onConfirm = onAddTrain,
            addFailedMessage = stringResource(R.string.add_train_fail_msg)
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
        } else if (uiState.trains.isEmpty()) {
            ListPlaceholder(
                message = stringResource(R.string.empty_train_screen_message),
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
        } else {
            TrainList(
                trains = uiState.trains,
                modifier = modifier,
                onSwipeToDelete = {
                    onDeleteTrain(it)
                }
            )
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Train")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListPopulatedPreview() {
    var isLoading by remember { mutableStateOf(false) }
    val trains = List(20) {
        Train(
            "$it", "Route $it", emptyList(), lastUpdated = OffsetDateTime.now()
        )
    }
    val trainUiState = TrainUiState(isRefreshing = isLoading, trains = trains)
    val scope = rememberCoroutineScope()
    AmtrakerTheme {
        TrainScreenContent(
            trainUiState,
            validateTrainNum = { it.isNotEmpty() && it.all(Char::isDigit) },
            onAddTrain = { true },
            onDeleteTrain = {},
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
fun TrainListEmptyPreview() {
    AmtrakerTheme {
        TrainScreenContent(
            TrainUiState(),
            validateTrainNum = { it.isNotEmpty() && it.all(Char::isDigit) },
            onAddTrain = { true },
            onDeleteTrain = {},
            onRefresh = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListErrorPreview() {
    AmtrakerTheme {
        TrainScreenContent(
            TrainUiState(errorMessage = "This is an error message on the trains screen."),
            validateTrainNum = { it.isNotEmpty() && it.all(Char::isDigit) },
            onAddTrain = { false },
            onDeleteTrain = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListLoadingPreview() {
    AmtrakerTheme {
        TrainScreenContent(
            TrainUiState(isRefreshing = true),
            validateTrainNum = { it.isNotEmpty() && it.all(Char::isDigit) },
            onAddTrain = { true },
            onDeleteTrain = {},
            onRefresh = {})
    }
}