package com.example.liftlog.userinterface.screens.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.liftlog.data.room.ExerciseEntity
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.launch

// Composable dialog used for both logging a new exercise and editing an existing one.
@Composable
fun ManualLogDialog(
    state: ExerciseStateHolder,
    // Optional parameter: if provided, the dialog is in 'edit' mode.
    initialExercise: ExerciseEntity? = null,
    // Callback to close the dialog.
    onDismiss: () -> Unit
) {
    // Determine if the dialog is in edit mode based on the presence of initial exercise data.
    val isEditing = initialExercise != null

    // State variables initialized using existing data if editing, or empty strings if logging new.
    var exerciseName by remember { mutableStateOf(initialExercise?.exerciseName ?: "") }
    var muscleGroup by remember { mutableStateOf(initialExercise?.muscle ?: "") }
    // Convert numeric fields to String for display, handling nulls gracefully.
    var sets by remember { mutableStateOf(initialExercise?.sets?.toString() ?: "") }
    var reps by remember { mutableStateOf(initialExercise?.reps?.toString() ?: "") }
    var weight by remember { mutableStateOf(initialExercise?.weight?.toString() ?: "") }

    // Coroutine scope for launching database operations.
    val scope = rememberCoroutineScope()
    // Validation: the exercise name is mandatory and must not be blank.
    val isNameValid = exerciseName.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        // Dynamic title based on whether the user is editing or creating.
        title = { Text(if (isEditing) "Edit Exercise Log" else "Log New Exercise") },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                // Exercise Name Input (Mandatory)
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g., Squat") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Target Muscle Group Input (Optional)
                OutlinedTextField(
                    value = muscleGroup,
                    onValueChange = { muscleGroup = it },
                    label = { Text("Target Muscle Group (Optional)") },
                    placeholder = { Text("e.g., Quads, N/A") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Row for Sets, Reps, and Weight inputs.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sets input: filters to only allow digits.
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it.filter { c -> c.isDigit() } },
                        label = { Text("Sets") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Reps input: filters to only allow digits.
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it.filter { c -> c.isDigit() } },
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Weight input: filters to allow digits and the decimal point ('.').
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Weight") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isNameValid) {
                        scope.launch {
                            val name = exerciseName.trim()
                            // Default to "N/A" if the muscle group is empty.
                            val muscle = muscleGroup.trim().takeIf { it.isNotEmpty() } ?: "N/A"

                            // Safely convert input strings to nullable numeric types.
                            val setsInt = sets.toIntOrNull()
                            val repsInt = reps.toIntOrNull()
                            val weightDouble = weight.toDoubleOrNull()

                            if (isEditing) {
                                // If editing, call the update function with the existing ID.
                                state.updateExercise(
                                    id = initialExercise.id,
                                    name = name,
                                    muscle = muscle,
                                    sets = setsInt,
                                    reps = repsInt,
                                    weight = weightDouble
                                )
                            } else {
                                // If creating new, call the save function.
                                state.saveExercise(name, muscle, setsInt, repsInt, weightDouble)
                            }

                            onDismiss() // Close dialog after successful operation.
                        }
                    }
                },
                // Button is only enabled if the Exercise Name is valid.
                enabled = isNameValid
            ) {
                // Dynamic button text.
                Text(if (isEditing) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}