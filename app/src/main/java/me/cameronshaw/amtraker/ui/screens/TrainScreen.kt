package me.cameronshaw.amtraker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cameronshaw.amtraker.R
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.ui.common.AddItemDialog
import me.cameronshaw.amtraker.ui.screens.trains.components.TrainListItem
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme
import java.time.OffsetDateTime

@Composable
fun TrainScreen(
    viewModel: TrainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
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

    if (showAddDialog) {
        AddItemDialog(
            title = stringResource(R.string.add_train_title),
            fieldLabel = stringResource(R.string.add_train_field),
            onDismissRequest = { showAddDialog = false },
            validateInput = validateTrainNum,
            onConfirm = onAddTrain
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (!uiState.errorMessage.isNullOrEmpty()) {
            ListPlaceholder(
                message = uiState.errorMessage,
                isError = true,
                modifier = modifier
            )
        } else if (uiState.trains.isEmpty()) {
            ListPlaceholder(
                message = stringResource(R.string.empty_train_screen_message), modifier = modifier
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

@Composable
fun TrainList(
    trains: List<Train>, modifier: Modifier = Modifier, onSwipeToDelete: (Train) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(trains, key = { it.num }) { train ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onSwipeToDelete(train)
                        true
                    } else {
                        false
                    }
                }
            )
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true,
                backgroundContent = {
                    val color = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                        else -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .background(color),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.CenterEnd)
                                    .padding(12.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            ) {
                TrainListItem(train = train)
            }
        }
    }
}

@Composable
fun ListPlaceholder(
    message: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListPopulatedPreview() {
    val trains = List(20) {
        Train(
            "$it", "Route $it", emptyList(), lastUpdated = OffsetDateTime.now()
        )
    }
    val trainUiState = TrainUiState(trains = trains)
    AmtrakerTheme {
        TrainScreenContent(
            trainUiState,
            validateTrainNum = { true },
            onAddTrain = { true },
            onDeleteTrain = {},
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrainListEmptyPreview() {
    AmtrakerTheme {
        TrainScreenContent(
            TrainUiState(),
            validateTrainNum = { true },
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
            validateTrainNum = { true },
            onAddTrain = { true },
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
            TrainUiState(isLoading = true),
            validateTrainNum = { true },
            onAddTrain = { true },
            onDeleteTrain = {},
            onRefresh = {})
    }
}