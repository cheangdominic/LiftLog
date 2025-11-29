package com.example.liftlog.userinterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.liftlog.navigation.Screen
import com.example.liftlog.state.ExerciseStateHolder
import com.example.liftlog.userinterface.screens.dialogs.ManualLogDialog

// Extension function to capitalize the first letter of every word in a string.
fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

// Main screen displaying a list of exercises fetched from the API.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(state: ExerciseStateHolder, nav: NavController) {
    // State to control the visibility of the manual exercise logging dialog.
    var showManualLogDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            // Floating Action Button to trigger the manual log dialog.
            FloatingActionButton(onClick = { showManualLogDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Manual Exercise")
            }
        },
        topBar = {
            // Top Bar component for the screen title.
            CenterAlignedTopAppBar(
                title = { Text("Discover Exercises", fontWeight = FontWeight.Bold) },
            )
        }
    ) { paddingValues ->
        // LazyColumn efficiently displays a potentially large, scrollable list of exercises.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Iterate through the list of exercises from the API state.
            items(state.apiExercises) { ex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        // Navigate to the DetailScreen when an exercise card is clicked.
                        .clickable { nav.navigate(Screen.Detail.createRoute(ex.id)) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F2F2))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Exercise Name
                        Text(
                            text = ex.name.capitalizeWords(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1A1A1A)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Target Muscle Group
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

    // Display the ManualLogDialog, which is now imported from its own file.
    if (showManualLogDialog) {
        ManualLogDialog(
            state = state,
            onDismiss = { showManualLogDialog = false } // Close the dialog on dismissal.
        )
    }
}