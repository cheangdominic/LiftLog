package com.example.liftlog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.liftlog.api.ApiModule
import com.example.liftlog.api.ExerciseApi
import com.example.liftlog.data.ExerciseRepository
import com.example.liftlog.data.room.ExerciseDatabase
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Primary entry point for the Android application, inheriting from ComponentActivity
class MainActivity : ComponentActivity() {

    // The central state management object for the entire application.
    private lateinit var stateHolder: ExerciseStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Room database instance using the application context.
        val db = ExerciseDatabase.getInstance(applicationContext)

        try {
            // 1. Initialize the Ktor-based API service for remote data fetching.
            val exerciseService = ExerciseApi(ApiModule.apiClient)

            // 2. Initialize the Repository, which mediates between the API (remote)
            //    and Room DAOs (local). It acts as a single source of truth.
            val repo = ExerciseRepository(
                exerciseService,
                db.exerciseDao(),
                db.separatorDao(),
                db.historyDao()
            )

            // 3. Initialize the State Holder, responsible for holding the application's
            //    state (e.g., list of exercises, history) and interacting with the Repository.
            stateHolder = ExerciseStateHolder(repo)

            // Use the lifecycleScope to launch coroutines that are automatically
            // canceled when this activity is destroyed.
            lifecycleScope.launch(Dispatchers.IO) {
                // Load remote exercise data from the API in the background.
                stateHolder.loadApiExercises()
                // Load local data (history, separators) from the Room database.
                stateHolder.loadSavedData()
            }

            // Set the Compose UI content for the activity.
            setContent {
                // Pass the initialized state holder to the root Composable of the application.
                WorkoutApp(stateHolder)
            }
        } catch (e: Exception) {
            // Catch and log any initialization errors (e.g., API setup failure).
            Log.e("MainActivity", "Failed to initialize API or Repository: ${e.message}", e)

        }
    }
}