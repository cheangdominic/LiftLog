package com.example.liftlog.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExerciseDao {

    @Insert
    suspend fun insert(entity: ExerciseEntity): Long

    @Query("SELECT * FROM exercise_log ORDER BY addedAt DESC")
    suspend fun getAll(): List<ExerciseEntity>

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Query("DELETE FROM exercise_log WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM exercise_log")
    suspend fun clearAll()
}