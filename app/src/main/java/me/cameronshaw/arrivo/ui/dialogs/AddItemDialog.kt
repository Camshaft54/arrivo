package me.cameronshaw.arrivo.ui.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.launch

/**
 * A generic dialog for adding a new item. It includes a text field,
 * input validation, and handles submission success or failure.
 *
 * @param title The title of the dialog (e.g., "Add New Train").
 * @param fieldLabel The label for the text input field (e.g., "Train Number").
 * @param onDismissRequest A lambda to be invoked when the dialog should be dismissed.
 * @param validateInput A function that takes the current text and returns true if it's valid.
 * @param onConfirm A suspend function that attempts to add the item. It should return
 * true on success (which dismisses the dialog) and false on failure.
 */
@Composable
fun AddItemDialog(
    title: String,
    fieldLabel: String,
    onDismissRequest: () -> Unit,
    validateInput: (String) -> Boolean,
    onConfirm: suspend (String) -> Boolean,
    modifier: Modifier = Modifier,
    addFailedMessage: String,
    suggestions: Map<String, String> = emptyMap(),
    helpTitle: String? = null,
    helpContent: (@Composable () -> Unit)? = null
) {
    var text by remember { mutableStateOf("") }
    var isInputValid by remember { mutableStateOf(false) }
    var submissionError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    val matchingSuggestions = remember(text) {
        if (text.isEmpty() || suggestions.containsKey(text)) emptyList()
        else suggestions.entries.filter {
            it.key.startsWith(text, ignoreCase = true) ||
                    it.value.contains(text, ignoreCase = true)
        }.take(5)
    }

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(matchingSuggestions) {
        dropdownExpanded = matchingSuggestions.isNotEmpty()
    }

    BackHandler {
        if (!isLoading) {
            onDismissRequest()
        }
    }

    if (showHelpDialog && helpContent != null && helpTitle != null) {
        HelpDialog(
            title = helpTitle,
            content = helpContent,
            onDismissRequest = { showHelpDialog = false }
        )
    }

    AlertDialog(
        onDismissRequest = {
            // Don't dismiss if it's in the middle of a network request
            if (!isLoading) onDismissRequest()
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title)
                if (helpContent != null) {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Help")
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            isInputValid = validateInput(it)
                            if (submissionError != null) submissionError = null
                        },
                        label = { Text(fieldLabel) },
                        singleLine = true,
                        isError = !isInputValid && text.isNotEmpty(),
                        modifier = modifier.focusRequester(focusRequester).fillMaxWidth()
                    )

                    // Simple dropdown implementation
                    if (dropdownExpanded) {
                        DropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            properties = PopupProperties(focusable = false)
                        ) {
                            matchingSuggestions.forEach { (code, name) ->
                                DropdownMenuItem(
                                    text = { Text("$name ($code)") },
                                    onClick = {
                                        text = code
                                        isInputValid = validateInput(code)
                                    }
                                )
                            }
                        }
                    }
                }
                // Show submission error message if it exists
                if (submissionError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = submissionError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        val success = onConfirm(text)
                        isLoading = false
                        if (success) {
                            onDismissRequest()
                        } else {
                            submissionError = addFailedMessage
                        }
                    }
                },
                // Disable the button if input is invalid or it's loading
                enabled = isInputValid && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp))
                } else {
                    Text("Add")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddItemDialogPreview() {
    MaterialTheme {
        AddItemDialog(
            title = "Add New Train",
            fieldLabel = "Train Number",
            onDismissRequest = {},
            validateInput = { it.isNotEmpty() && it.all(Char::isDigit) },
            onConfirm = { true },
            addFailedMessage = "Failed to add item. Please try again." // Simulate success
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemDialogWithHelpPreview() {
    MaterialTheme {
        AddItemDialog(
            title = "Add New Train",
            fieldLabel = "Train Number",
            onDismissRequest = {},
            validateInput = { it.isNotEmpty() && it.all(Char::isDigit) },
            onConfirm = { true },
            addFailedMessage = "Failed to add item. Please try again.",
            helpTitle = "Help",
            helpContent = {
                Text("This is some help text.")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemDialogSubmissionErrorPreview() {
    MaterialTheme {
        AddItemDialog(
            title = "Add New Train",
            fieldLabel = "Train Number",
            onDismissRequest = {},
            validateInput = { true },
            onConfirm = { false },
            addFailedMessage = "Failed to add item. Please try again." // Simulate failure
        )
    }
}