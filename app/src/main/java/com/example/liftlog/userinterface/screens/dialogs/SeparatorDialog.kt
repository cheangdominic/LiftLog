package com.example.liftlog.userinterface.screens.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.launch

// Composable dialog used to edit the text of an existing history separator.
@Composable
fun SeparatorDialog(
    state: ExerciseStateHolder,
    // The separator item containing the current ID and initial text.
    initialSeparator: ExerciseStateHolder.HistoryItem.Separator,
    // Callback to close the dialog.
    onDismiss: () -> Unit
) {
    // Extract the initial text to populate the TextField.
    val initialText = initialSeparator.text
    // State to hold the user's potentially updated separator text.
    var newSeparatorText by remember { mutableStateOf(initialText) }
    // Coroutine scope for running the update operation.
    val scope = rememberCoroutineScope()
    // Validation: the new text must not be empty after trimming whitespace.
    val isTextValid = newSeparatorText.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Separator Name") },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = newSeparatorText,
                    onValueChange = { newSeparatorText = it },
                    label = { Text("Separator Name") },
                    placeholder = { Text("e.g., Leg Day, Week 3") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isTextValid) {
                        scope.launch {
                            // Call the state holder to update the separator in the database.
                            state.updateSeparator(
                                initialSeparator.separatorId,
                                newSeparatorText.trim()
                            )
                            onDismiss() // Close the dialog upon successful update.
                        }
                    }
                },
                // The Update button is only enabled if the text input is valid.
                enabled = isTextValid
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}