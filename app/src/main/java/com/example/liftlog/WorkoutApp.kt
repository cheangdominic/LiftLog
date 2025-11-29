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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutApp(stateHolder: ExerciseStateHolder) {
    val nav = rememberNavController()

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNav(nav) }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Screen.Exercises.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Exercises.route) {
                ExercisesScreen(stateHolder, nav)
            }

            composable(Screen.Detail.route) { backStack ->
                val id = backStack.arguments?.getString("id") ?: ""
                DetailScreen(stateHolder, id, nav)
            }

            composable(Screen.History.route) {
                HistoryScreen(stateHolder)
            }
        }
    }
}
