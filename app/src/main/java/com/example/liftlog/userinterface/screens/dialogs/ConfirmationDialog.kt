package com.example.liftlog.userinterface.screens.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// A generic composable dialog used to ask the user for confirmation before proceeding with an action.
@Composable
fun ConfirmationDialog(
    // The title of the dialog (e.g., "Confirm Deletion").
    title: String,
    // The main message/question (e.g., "Are you sure you want to delete this item?").
    text: String,
    // Action to execute if the user confirms (e.g., delete the item).
    onConfirm: () -> Unit,
    // Action to execute if the user dismisses the dialog.
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            // Display the title with large typography.
            Text(title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            // Display the main content text.
            Text(text, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            // The primary action button (usually for the destructive/confirming action).
            Button(
                onClick = {
                    onConfirm() // Execute the confirmation action.
                    onDismiss() // Close the dialog immediately after confirmation.
                },
                // Use the error color scheme to visually emphasize a destructive action like Delete.
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                // Label the button clearly as "Delete" and use white text for contrast.
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            // The secondary action button for canceling or dismissing the dialog.
            TextButton(
                onClick = onDismiss // Only closes the dialog without executing the main action.
            ) {
                Text("Cancel")
            }
        }
    )
}