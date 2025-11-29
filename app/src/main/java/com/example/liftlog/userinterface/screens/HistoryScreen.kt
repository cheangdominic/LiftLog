package com.example.liftlog.userinterface.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.liftlog.state.ExerciseStateHolder
import com.example.liftlog.data.room.ExerciseEntity
import kotlinx.coroutines.launch
import com.example.liftlog.userinterface.screens.dialogs.ManualLogDialog
import com.example.liftlog.userinterface.screens.dialogs.SeparatorDialog
import com.example.liftlog.userinterface.screens.dialogs.ConfirmationDialog

/**
 * Screen to display the user's workout history, including exercises and separators.
 *
 * @param state The ExerciseStateHolder which holds and manages the history data.
 */
@SuppressLint("DefaultLocale")
@Composable
fun HistoryScreen(state: ExerciseStateHolder) {
    // State for the text input field used to add new separators.
    var newSeparator by remember { mutableStateOf("") }
    // Coroutine scope for executing asynchronous operations (like DB calls).
    val scope = rememberCoroutineScope()
    // Define the state for controlling the scroll position of the LazyColumn.
    val listState = rememberLazyListState()

    // State for exercise editing dialog.
    var showEditExerciseDialog by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<ExerciseEntity?>(null) }

    // State for separator editing dialog.
    var showEditSeparatorDialog by remember { mutableStateOf(false) }
    var separatorToEdit by remember { mutableStateOf<ExerciseStateHolder.HistoryItem.Separator?>(null) }

    // State for deletion confirmation dialog.
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var itemToDeleteId by remember { mutableStateOf<Int?>(null) }
    // Flag to differentiate between deleting an Exercise or a Separator.
    var isDeletingExercise by remember { mutableStateOf(true) }


    Column(modifier = Modifier.fillMaxSize()) {
        // --- Top Control Bar: Separator Input & Clear All Button ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text field for entering a new separator name.
            BasicTextField(
                value = newSeparator,
                onValueChange = { newSeparator = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                singleLine = true
            )

            // Button to add the new separator to the history.
            Button(onClick = {
                if (newSeparator.isNotBlank()) {
                    scope.launch {
                        state.addSeparator(newSeparator)
                        // Scroll to the first item (index 0) after adding the separator.
                        listState.animateScrollToItem(0)
                        newSeparator = "" // Clear the input field after adding.
                    }
                }
            }) {
                Text("Add Separator")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Button to clear the entire workout history.
            Button(onClick = {
                scope.launch {
                    state.clearAll()
                }
            }) {
                Text("Clear All")
            }
        }

        // --- LazyColumn: Displaying the Workout History List ---
        LazyColumn(
            // Use the listState to control scrolling.
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            // Display items from the history list, which contains both Exercises and Separators.
            items(
                state.historyItems,
                // Use a composite key (type prefix + ID) to ensure uniqueness and stability,
                // as IDs might be duplicated across ExerciseEntity and Separator entities.
                key = {
                    when (it) {
                        is ExerciseStateHolder.HistoryItem.Exercise -> "ex_${it.ex.id}"
                        is ExerciseStateHolder.HistoryItem.Separator -> "sep_${it.separatorId}"
                    }
                }
            ) { item ->
                when (item) {
                    // Case for rendering an individual logged exercise.
                    is ExerciseStateHolder.HistoryItem.Exercise -> {
                        var expanded by remember { mutableStateOf(false) } // State for the exercise dropdown menu.

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Exercise Name
                                    Text(
                                        text = item.ex.exerciseName.capitalizeWords(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Details Row (Sets, Reps, Weight)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(0.9f),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Sets: ${item.ex.sets ?: "-"}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF666666)
                                        )
                                        Text(
                                            text = "Reps: ${item.ex.reps ?: "-"}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF666666)
                                        )
                                        Text(
                                            // Format weight to one decimal place if present.
                                            text = "Weight: ${item.ex.weight?.let { String.format("%.1f", it) } ?: "-"} lbs",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF666666)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Target Muscle Group
                                    Text(
                                        text = "Muscle: ${item.ex.muscle?.capitalizeWords()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF666666)
                                    )
                                }

                                // Dropdown menu for Edit/Delete options.
                                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            Icons.Filled.MoreVert,
                                            contentDescription = "Options"
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                expanded = false
                                                exerciseToEdit = item.ex
                                                showEditExerciseDialog = true
                                            }
                                        )
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("Delete") },
                                            onClick = {
                                                expanded = false
                                                itemToDeleteId = item.ex.id
                                                isDeletingExercise = true
                                                showDeleteConfirmation = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Case for rendering a user-added separator.
                    is ExerciseStateHolder.HistoryItem.Separator -> {
                        var expanded by remember { mutableStateOf(false) } // State for the separator dropdown menu.

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            // Distinct color for separators for visual differentiation.
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Separator Text
                                Text(
                                    text = item.text.capitalizeWords(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFF1A1A1A)
                                )

                                // Dropdown menu for Edit/Delete options.
                                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            Icons.Filled.MoreVert,
                                            contentDescription = "Options"
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                expanded = false
                                                separatorToEdit = item
                                                showEditSeparatorDialog = true
                                            }
                                        )
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("Delete") },
                                            onClick = {
                                                expanded = false
                                                itemToDeleteId = item.separatorId
                                                isDeletingExercise = false
                                                showDeleteConfirmation = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Conditional Dialog Rendering ---

    // 1. ManualLogDialog for editing an existing Exercise.
    if (showEditExerciseDialog && exerciseToEdit != null) {
        ManualLogDialog(
            state = state,
            // Pass the existing exercise data to pre-fill the form.
            initialExercise = exerciseToEdit!!,
            onDismiss = { showEditExerciseDialog = false; exerciseToEdit = null }
        )
    }

    // 2. SeparatorDialog for editing an existing Separator.
    if (showEditSeparatorDialog && separatorToEdit != null) {
        SeparatorDialog(
            state = state,
            // Pass the existing separator data to pre-fill the form.
            initialSeparator = separatorToEdit!!,
            onDismiss = { showEditSeparatorDialog = false; separatorToEdit = null }
        )
    }

    // 3. ConfirmationDialog for deleting an Exercise or Separator.
    if (showDeleteConfirmation && itemToDeleteId != null) {
        val idToDelete = itemToDeleteId!!
        val itemName = if (isDeletingExercise) "exercise" else "separator"
        ConfirmationDialog(
            title = "Confirm Deletion",
            text = "Are you sure you want to permanently delete this $itemName from your history?",
            onConfirm = {
                scope.launch {
                    // Call the appropriate delete function based on the flag.
                    if (isDeletingExercise) {
                        state.deleteExercise(idToDelete)
                    } else {
                        state.deleteSeparator(idToDelete)
                    }
                }
                itemToDeleteId = null // Clear state after action
                showDeleteConfirmation = false
            },
            onDismiss = {
                itemToDeleteId = null // Clear state on dismiss
                showDeleteConfirmation = false
            }
        )
    }
}