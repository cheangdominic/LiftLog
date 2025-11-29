package com.example.liftlog.userinterface.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType

// Composable screen for viewing exercise details and logging a workout entry.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: ExerciseStateHolder,
    exerciseId: String,
    nav: NavController
) {
    // Attempt to find the full exercise object using the passed ID.
    // If the exercise is not found, the function returns early.
    val ex = state.apiExercises.find { it.id == exerciseId } ?: return

    // Coroutine scope for launching asynchronous operations (like saving data).
    val scope = rememberCoroutineScope()

    // State variables for user input fields (Sets, Reps, Weight).
    // They are initialized as empty strings.
    var sets by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Display the name of the exercise, capitalized.
                    Text(
                        ex.name.capitalizeWords(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    // Back button to return to the previous screen (ExercisesScreen).
                    IconButton(onClick = {
                        nav.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                // Custom colors for this specific Top Bar.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Card displaying static exercise information (like target muscle).
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Target Muscle:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ex.target?.capitalizeWords() ?: "Unknown",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Row containing the three input fields for logging.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sets input field. Filters input to only allow digits.
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it.filter { c -> c.isDigit() } },
                    label = { Text("Sets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))

                // Reps input field. Filters input to only allow digits.
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter { c -> c.isDigit() } },
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))

                // Weight input field. Filters input to allow digits and the decimal point ('.').
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Weight (lbs)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Button to save the logged workout data.
            Button(
                onClick = {
                    // Launch a coroutine to perform the database operation asynchronously.
                    scope.launch {
                        // Safely convert input strings to their corresponding numeric types,
                        // resulting in null if conversion fails (e.g., empty string).
                        val setsInt = sets.toIntOrNull()
                        val repsInt = reps.toIntOrNull()
                        val weightDouble = weight.toDoubleOrNull()

                        // Call the state holder function to save the new exercise log.
                        state.saveExercise(ex.name, ex.target, setsInt, repsInt, weightDouble)
                        // Navigate back to the previous screen (ExercisesScreen) after saving.
                        nav.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Add to Log",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}