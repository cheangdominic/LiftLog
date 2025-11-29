package com.example.liftlog.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Data Access Object (DAO) for the HistoryItemEntity, managing the order of exercises and separators.
@Dao
interface HistoryDao {

    // Retrieves all history items from the 'history_order' table.
    // The results are ordered by the 'position' column, ensuring the history is displayed chronologically or as last saved.
    @Query("SELECT * FROM history_order ORDER BY position ASC")
    suspend fun getAllOrdered(): List<HistoryItemEntity>

    // Inserts a list of HistoryItemEntity objects.
    // OnConflictStrategy.REPLACE ensures that if an item with the same primary key exists (which is 'position' in this context),
    // it is replaced by the new item, allowing for atomic updates to the entire history order.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HistoryItemEntity>)

    // Deletes all entries from the history order table, effectively clearing the saved display order.
    @Query("DELETE FROM history_order")
    suspend fun clearAll()
}