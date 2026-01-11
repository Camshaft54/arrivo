package me.cameronshaw.arrivo.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.cameronshaw.arrivo.ui.theme.StatusBlue
import me.cameronshaw.arrivo.ui.theme.StatusGreen
import me.cameronshaw.arrivo.ui.theme.StatusRed

@Composable
fun AppHelpDialog(
    onDismissRequest: () -> Unit
) {HelpDialog(
    title = "Arrive Help",
    content = {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Welcome to Arrivo. Monitor your commute by creating a personalized schedule for the trains and stations that matter to you.",
                style = MaterialTheme.typography.bodyMedium
            )

            HelpSection(
                title = "Getting Started",
                description = "Add train numbers on the Trains page. If a train is currently in service, the status indicator will turn green."
            )

            HelpSection(
                title = "Monitoring Stations",
                description = "Add stations along your route. Arrivo automatically calculates the most relevant departure and arrival times for your schedule."
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Status Indicators",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusRow(StatusGreen, "On Time", "Train is performing as scheduled.")
                StatusRow(StatusBlue, "Early", "Train is ahead of schedule.")
                StatusRow(StatusRed, "Late", "Train is experiencing a delay.")
                StatusRow(
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    "Unknown",
                    "Train status is not available."
                )
            }

            HelpSection(
                title = "Home Screen Widget",
                description = "Add the Arrivo widget to your home screen for instant updates. To ensure reliability, allow background usage and disable battery optimization for Arrivo in your device settings."
            )
        }
    },
    onDismissRequest = onDismissRequest
)
}

@Composable
private fun HelpSection(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatusRow(color: Color, label: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Column {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppHelpDialogPreview() {
    MaterialTheme {
        AppHelpDialog(
            onDismissRequest = {}
        )
    }
}