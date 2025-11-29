package com.example.liftlog.userinterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.liftlog.navigation.Screen
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.launch

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(state: ExerciseStateHolder, nav: NavController) {
    var showManualLogDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showManualLogDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Manual Exercise")
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Discover Exercises", fontWeight = FontWeight.Bold) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(state.apiExercises) { ex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { nav.navigate(Screen.Detail.createRoute(ex.id)) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = ex.name.capitalizeWords(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Target: ${ex.target?.capitalizeWords() ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }

    if (showManualLogDialog) {
        ManualLogDialog(
            state = state,
            onDismiss = { showManualLogDialog = false }
        )
    }
}

@Composable
fun ManualLogDialog(
    state: ExerciseStateHolder,
    onDismiss: () -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val isNameValid = exerciseName.trim().isNotEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log New Exercise") },
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

                            state.saveExercise(name, muscle, setsInt, repsInt, weightDouble)
                            onDismiss()
                        }
                    }
                },
                enabled = isNameValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}