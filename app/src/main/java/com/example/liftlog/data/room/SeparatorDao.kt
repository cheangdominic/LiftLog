package com.example.liftlog.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Data Access Object (DAO) for the SeparatorEntity, defining the methods for database interaction.
@Dao
interface SeparatorDao {

    // Inserts a new SeparatorEntity into the database.
    @Insert
    suspend fun insert(separator: SeparatorEntity): Long

    // Retrieves all separator entities from the database.
    @Query("SELECT * FROM separator_table")
    suspend fun getAll(): List<SeparatorEntity>

    // Deletes all entries from the separator table.
    @Query("DELETE FROM separator_table")
    suspend fun clearAll()

    // Updates an existing SeparatorEntity. It matches the entity by its primary key (id).
    @Update
    suspend fun update(separator: SeparatorEntity)

    // Deletes a specific separator entity based on its ID.
    @Query("DELETE FROM separator_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}