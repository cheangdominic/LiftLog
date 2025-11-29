package com.example.liftlog.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defines the data structure for a user-defined separator or "break" in the workout history.
// tableName specifies the name of the table on disk.
@Entity(tableName = "separator_table")
data class SeparatorEntity(
    // Primary key for the table. autoGenerate = true allows Room to generate a unique ID.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // The text content of the separator (e.g., "Leg Day", "Start of Week 3").
    val text: String,
)