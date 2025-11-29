package com.example.liftlog.userinterface.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@SuppressLint("DefaultLocale")
@Composable
fun HistoryScreen(state: ExerciseStateHolder) {
    var newSeparator by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    var showEditExerciseDialog by remember { mutableStateOf(false) }
    var exerciseToEdit by remember { mutableStateOf<ExerciseEntity?>(null) }

    var showEditSeparatorDialog by remember { mutableStateOf(false) }
    var separatorToEdit by remember { mutableStateOf<ExerciseStateHolder.HistoryItem.Separator?>(null) }

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var itemToDeleteId by remember { mutableStateOf<Int?>(null) }
    var isDeletingExercise by remember { mutableStateOf(true) }


    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Button(onClick = {
                if (newSeparator.isNotBlank()) {
                    scope.launch {
                        state.addSeparator(newSeparator)
                        newSeparator = ""
                    }
                }
            }) {
                Text("Add Separator")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                scope.launch {
                    state.clearAll()
                }
            }) {
                Text("Clear All")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            items(state.historyItems, key = { it.hashCode() }) { item ->
                when (item) {
                    is ExerciseStateHolder.HistoryItem.Exercise -> {
                        var expanded by remember { mutableStateOf(false) }

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
                                    Text(
                                        text = item.ex.exerciseName.capitalizeWords(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

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
                                            text = "Weight: ${item.ex.weight?.let { String.format("%.1f", it) } ?: "-"} lbs",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF666666)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Muscle: ${item.ex.muscle?.capitalizeWords()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF666666)
                                    )
                                }

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

                    is ExerciseStateHolder.HistoryItem.Separator -> {
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.text.capitalizeWords(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFF1A1A1A)
                                )

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

    if (showEditExerciseDialog && exerciseToEdit != null) {
        ManualLogDialog(
            state = state,
            initialExercise = exerciseToEdit!!,
            onDismiss = { showEditExerciseDialog = false; exerciseToEdit = null }
        )
    }

    if (showEditSeparatorDialog && separatorToEdit != null) {
        SeparatorDialog(
            state = state,
            initialSeparator = separatorToEdit!!,
            onDismiss = { showEditSeparatorDialog = false; separatorToEdit = null }
        )
    }

    if (showDeleteConfirmation && itemToDeleteId != null) {
        val idToDelete = itemToDeleteId!!
        val itemName = if (isDeletingExercise) "exercise" else "separator"
        ConfirmationDialog(
            title = "Confirm Deletion",
            text = "Are you sure you want to permanently delete this $itemName from your history?",
            onConfirm = {
                scope.launch {
                    if (isDeletingExercise) {
                        state.deleteExercise(idToDelete)
                    } else {
                        state.deleteSeparator(idToDelete)
                    }
                }
                itemToDeleteId = null
                showDeleteConfirmation = false
            },
            onDismiss = {
                itemToDeleteId = null
                showDeleteConfirmation = false
            }
        )
    }
}
