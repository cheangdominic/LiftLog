package com.example.liftlog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.liftlog.navigation.Screen
import com.example.liftlog.state.ExerciseStateHolder
import com.example.liftlog.userinterface.components.BottomNav
import com.example.liftlog.userinterface.components.TopBar
import com.example.liftlog.userinterface.screens.DetailScreen
import com.example.liftlog.userinterface.screens.ExercisesScreen
import com.example.liftlog.userinterface.screens.HistoryScreen

// Main Composable function that defines the structure and navigation of the entire app.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutApp(stateHolder: ExerciseStateHolder) {
    // Controller for managing application navigation state.
    val nav = rememberNavController()

    // Scaffold provides the basic structure for the screen (TopBar, BottomBar, Content).
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNav(nav) }
    ) { padding ->
        // NavHost defines the navigation graph, linking routes to Composable screens.
        NavHost(
            navController = nav,
            // Sets the initial screen displayed when the app starts.
            startDestination = Screen.Exercises.route,
            // Applies padding from the Scaffold (to prevent content drawing under bars).
            modifier = Modifier.padding(padding)
        ) {

            // Route for the main Exercises list screen.
            composable(Screen.Exercises.route) {
                ExercisesScreen(stateHolder, nav)
            }

            // Route for the Detail screen, expecting an 'id' argument in the path.
            composable(Screen.Detail.route) { backStack ->
                // Extract the 'id' argument from the navigation back stack entry.
                val id = backStack.arguments?.getString("id") ?: ""
                DetailScreen(stateHolder, id, nav)
            }

            // Route for the workout History screen.
            composable(Screen.History.route) {
                HistoryScreen(stateHolder)
            }
        }
    }
}