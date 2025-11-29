package com.example.liftlog.userinterface.screens.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.launch

@Composable
fun SeparatorDialog(
    state: ExerciseStateHolder,
    initialSeparator: ExerciseStateHolder.HistoryItem.Separator,
    onDismiss: () -> Unit
) {
    val initialText = initialSeparator.text
    var newSeparatorText by remember { mutableStateOf(initialText) }
    val scope = rememberCoroutineScope()
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
                            state.updateSeparator(initialSeparator.separatorId, newSeparatorText.trim())
                            onDismiss()
                        }
                    }
                },
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