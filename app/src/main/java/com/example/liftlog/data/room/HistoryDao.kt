package com.example.liftlog.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_order ORDER BY position ASC")
    suspend fun getAllOrdered(): List<HistoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HistoryItemEntity>)

    @Query("DELETE FROM history_order")
    suspend fun clearAll()
}