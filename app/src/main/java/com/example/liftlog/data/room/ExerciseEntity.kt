package com.example.liftlog.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_log")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseName: String,
    val muscle: String?,
    val sets: Int? = null,
    val reps: Int? = null,
    val weight: Double? = null,
    val addedAt: Long = System.currentTimeMillis()
)