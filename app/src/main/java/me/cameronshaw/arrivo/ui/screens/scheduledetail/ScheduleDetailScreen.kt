package me.cameronshaw.arrivo.ui.screens.scheduledetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import me.cameronshaw.arrivo.R
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.ui.screens.scheduledetail.components.ScheduleDetailStopList
import me.cameronshaw.arrivo.ui.util.toUiString

@Composable
fun ScheduleDetailScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: ScheduleDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = viewModel.eventFlow) {
        viewModel.eventFlow.collectLatest { eventMessage ->
            snackbarHostState.showSnackbar(message = eventMessage)
        }
    }

    if (uiState.train != null) {
        ScheduleDetailScreenContent(
            train = uiState.train!!,
            modifier = modifier
        )
    } else if (!uiState.errorMessage.isNullOrEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = uiState.errorMessage!!,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ScheduleDetailScreenContent(
    train: Train, modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "${train.num} ${train.routeName}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "${stringResource(R.string.last_updated)} ${train.lastUpdated.toUiString()}",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "${train.velocity.toInt()} mph",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )
        ScheduleDetailStopList(Modifier, train.stops)
    }
}