package com.example.liftlog.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SeparatorDao {

    @Insert
    suspend fun insert(separator: SeparatorEntity)

    @Query("SELECT * FROM separator_table")
    suspend fun getAll(): List<SeparatorEntity>

    @Query("DELETE FROM separator_table")
    suspend fun clearAll()

    @Update
    suspend fun update(separator: SeparatorEntity)

    @Query("DELETE FROM separator_table WHERE id = :id")
    suspend fun deleteById(id: Int)
}