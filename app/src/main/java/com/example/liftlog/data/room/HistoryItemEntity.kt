package com.example.liftlog.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_order")
data class HistoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val itemId: Int,
    val position: Int
)