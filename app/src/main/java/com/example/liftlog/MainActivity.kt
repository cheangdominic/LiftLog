package com.example.liftlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.liftlog.api.ApiModule
import com.example.liftlog.data.ExerciseRepository
import com.example.liftlog.data.room.ExerciseDatabase
import com.example.liftlog.state.ExerciseStateHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var stateHolder: ExerciseStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = ExerciseDatabase.getInstance(applicationContext)

        val repo = ExerciseRepository(
            ApiModule.api,
            db.exerciseDao(),
            db.separatorDao(),
            db.historyDao()
        )

        stateHolder = ExerciseStateHolder(repo)

        lifecycleScope.launch(Dispatchers.IO) {
            stateHolder.loadApiExercises()
            stateHolder.loadSavedData()
        }

        setContent {
            WorkoutApp(stateHolder)
        }
    }
}