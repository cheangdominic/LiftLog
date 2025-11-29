package com.example.liftlog.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// Data Access Object (DAO) for the ExerciseEntity, defining the methods for database interaction.
@Dao
interface ExerciseDao {

    // Inserts a new ExerciseEntity into the database. Returns the row ID (Long).
    @Insert
    suspend fun insert(entity: ExerciseEntity): Long

    // Retrieves all exercise logs, ordered by the time they were added (most recent first).
    @Query("SELECT * FROM exercise_log ORDER BY addedAt DESC")
    suspend fun getAll(): List<ExerciseEntity>

    // Updates an existing ExerciseEntity. It matches the entity by its primary key (id).
    @Update
    suspend fun update(exercise: ExerciseEntity)

    // Deletes a specific exercise log based on its ID.
    @Query("DELETE FROM exercise_log WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Deletes all entries from the exercise log table.
    @Query("DELETE FROM exercise_log")
    suspend fun clearAll()
}