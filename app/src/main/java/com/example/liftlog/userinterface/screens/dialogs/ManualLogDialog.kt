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

@Composable
fun ManualLogDialog(
    state: ExerciseStateHolder,
    initialExercise: ExerciseEntity? = null,
    onDismiss: () -> Unit
) {
    val isEditing = initialExercise != null

    var exerciseName by remember { mutableStateOf(initialExercise?.exerciseName ?: "") }
    var muscleGroup by remember { mutableStateOf(initialExercise?.muscle ?: "") }
    var sets by remember { mutableStateOf(initialExercise?.sets?.toString() ?: "") }
    var reps by remember { mutableStateOf(initialExercise?.reps?.toString() ?: "") }
    var weight by remember { mutableStateOf(initialExercise?.weight?.toString() ?: "") }

    val scope = rememberCoroutineScope()
    val isNameValid = exerciseName.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Exercise Log" else "Log New Exercise") },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g., Squat") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = muscleGroup,
                    onValueChange = { muscleGroup = it },
                    label = { Text("Target Muscle Group (Optional)") },
                    placeholder = { Text("e.g., Quads, N/A") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it.filter { c -> c.isDigit() } },
                        label = { Text("Sets") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it.filter { c -> c.isDigit() } },
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

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
                            val muscle = muscleGroup.trim().takeIf { it.isNotEmpty() } ?: "N/A"

                            val setsInt = sets.toIntOrNull()
                            val repsInt = reps.toIntOrNull()
                            val weightDouble = weight.toDoubleOrNull()

                            if (isEditing) {
                                state.updateExercise(
                                    id = initialExercise.id,
                                    name = name,
                                    muscle = muscle,
                                    sets = setsInt,
                                    reps = repsInt,
                                    weight = weightDouble
                                )
                            } else {
                                state.saveExercise(name, muscle, setsInt, repsInt, weightDouble)
                            }

                            onDismiss()
                        }
                    }
                },
                enabled = isNameValid
            ) {
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