package me.cameronshaw.arrivo.ui.dialogs.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import me.cameronshaw.arrivo.R
import me.cameronshaw.arrivo.data.local.model.AppSettings
import me.cameronshaw.arrivo.ui.dialogs.settings.components.DropdownSetting
import me.cameronshaw.arrivo.ui.dialogs.settings.components.SettingItem
import me.cameronshaw.arrivo.ui.theme.ArrivoTheme

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val appSettings by viewModel.appSettings.collectAsState()

    SettingsDialogContent(
        onDismissRequest, appSettings, viewModel::updateTheme, viewModel::updateProvider, modifier
    )
}

@Composable
fun SettingsDialogContent(
    onDismissRequest: () -> Unit,
    appSettings: AppSettings,
    onUpdateTheme: (String) -> Unit,
    onProviderChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showProviderHelp by remember { mutableStateOf(false) }

    if (showProviderHelp) {
        AlertDialog(
            onDismissRequest = { showProviderHelp = false },
            title = { Text(stringResource(R.string.provider_help)) },
            text = {
                Column {
                    Text(stringResource(R.string.provider_help_amtrak_description))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.provider_help_amtraker_description))
                }
            },
            confirmButton = {
                TextButton(onClick = { showProviderHelp = false }) {
                    Text(stringResource(R.string.got_it))
                }
            })
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.9f) // Take 90% of screen width
                .wrapContentHeight()
        ) {
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Enable scrolling for long content
            ) {
                // Dialog Header
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings", style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close settings")
                    }
                }

                Spacer(modifier = modifier.height(16.dp))
                HorizontalDivider() // A visual separator
                Spacer(modifier = modifier.height(16.dp))

                SettingItem(
                    title = "App Theme"
                ) {
                    DropdownSetting(
                        label = stringResource(R.string.choose_theme),
                        options = listOf("System default", "Light", "Dark"),
                        selectedOption = when (appSettings.theme) {
                            "SYSTEM" -> "System default"
                            "LIGHT" -> "Light"
                            "DARK" -> "Dark"
                            else -> "System default"
                        },
                        onOptionSelected = { newTheme ->
                            onUpdateTheme(
                                when (newTheme) {
                                    "System default" -> "SYSTEM"
                                    "Light" -> "LIGHT"
                                    "Dark" -> "DARK"
                                    else -> "SYSTEM"
                                }
                            )
                        })
                }

                SettingItem(
                    title = "Provider"
                ) {
                    DropdownSetting(
                        label = stringResource(R.string.choose_provider),
                        options = listOf("Amtrak", "Amtraker (3rd party)"),
                        selectedOption = when (appSettings.dataProvider) {
                            "AMTRAK" -> "Amtrak"
                            "AMTRAKER" -> "Amtraker (3rd party)"
                            else -> "Amtrak"
                        },
                        onOptionSelected = { newProvider ->
                            onProviderChange(
                                when (newProvider) {
                                    "Amtrak" -> "AMTRAK"
                                    "Amtraker (3rd party)" -> "AMTRAKER"
                                    else -> "AMTRAK"
                                }
                            )
                        },
                        onHelpClick = {
                            showProviderHelp = true
                        })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingDialog() {
    ArrivoTheme {
        SettingsDialogContent(
            onDismissRequest = { },
            appSettings = AppSettings(),
            onUpdateTheme = { },
            onProviderChange = { })
    }
}