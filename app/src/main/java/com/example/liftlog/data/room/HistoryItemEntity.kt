package com.example.liftlog.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defines the data structure for storing the order of items (exercises and separators) in the workout history.
// This table links ExerciseEntity and SeparatorEntity to maintain the correct display sequence.
@Entity(tableName = "history_order")
data class HistoryItemEntity(
    // Primary key for the history order table, auto-generated.
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // The type of item being referenced: typically "EXERCISE" or "SEPARATOR".
    val type: String,
    // The ID of the actual item (either ExerciseEntity.id or SeparatorEntity.id).
    val itemId: Int,
    // The display position of this item in the history list (0 = top/most recent).
    // This column is crucial for ordering when rebuilding the history view.
    val position: Int
)