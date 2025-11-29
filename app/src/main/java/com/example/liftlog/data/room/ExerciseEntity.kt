package com.example.liftlog.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defines the data structure for the exercise log table in the Room database.
// tableName specifies the name of the table on disk.
@Entity(tableName = "exercise_log")
data class ExerciseEntity(
    // Primary key for the table. autoGenerate = true makes Room automatically generate a unique ID.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // The name of the exercise (e.g., "Deadlift", "Bench Press").
    val exerciseName: String,
    // The target muscle group (nullable).
    val muscle: String?,
    // Number of sets performed (nullable).
    val sets: Int? = null,
    // Number of repetitions performed (nullable).
    val reps: Int? = null,
    // Weight lifted in pounds or kilograms (nullable).
    val weight: Double? = null,
    // Timestamp when the exercise was added/logged, defaulting to the current time.
    val addedAt: Long = System.currentTimeMillis()
)