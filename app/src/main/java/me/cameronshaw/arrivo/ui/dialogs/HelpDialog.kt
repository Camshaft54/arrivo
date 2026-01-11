package me.cameronshaw.arrivo.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import me.cameronshaw.arrivo.R

@Composable
fun HelpDialog(
    title: String,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = content,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.got_it))
            }
        }
    )
}