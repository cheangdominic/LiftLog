package com.example.liftlog.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "separator_table")
data class SeparatorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
)