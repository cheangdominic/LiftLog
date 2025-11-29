package com.example.liftlog.data

import com.example.liftlog.api.ExerciseApi
import com.example.liftlog.data.room.ExerciseDao
import com.example.liftlog.data.room.ExerciseEntity
import com.example.liftlog.data.room.SeparatorDao
import com.example.liftlog.data.room.SeparatorEntity
import com.example.liftlog.data.room.HistoryDao
import com.example.liftlog.data.room.HistoryItemEntity

// Repository layer that acts as a single source of truth for all exercise-related data.
// It abstracts data access from the rest of the application, coordinating between the API and local database (Room).
class ExerciseRepository(
    // Dependencies injected via the constructor:
    private val api: ExerciseApi, // For fetching external exercise data.
    private val exerciseDao: ExerciseDao, // For CRUD operations on ExerciseEntity.
    private val separatorDao: SeparatorDao, // For CRUD operations on SeparatorEntity (workout breaks/labels).
    private val historyDao: HistoryDao // For managing the order of history items.
) {

    // --- API Operations ---

    // Fetches the list of exercises from the external API.
    suspend fun fetchExercises() = api.getAllExercises()


    // --- Exercise CRUD Operations ---

    // Saves a new exercise log to the database and returns its auto-generated ID.
    suspend fun saveExercise(exercise: ExerciseEntity): Int {
        // Room returns the row ID as a Long, cast to Int for consistency.
        val newId = exerciseDao.insert(exercise).toInt()
        return newId
    }

    // Updates an existing exercise log in the database.
    suspend fun updateExercise(exercise: ExerciseEntity) {
        exerciseDao.update(exercise)
    }

    // Deletes an exercise log by its ID.
    suspend fun deleteExercise(id: Int) {
        exerciseDao.deleteById(id)
    }

    // Retrieves all saved exercise logs from the database.
    suspend fun getSavedExercises() = exerciseDao.getAll()


    // --- Separator CRUD Operations ---

    // Updates an existing separator entity (e.g., changes its text).
    suspend fun updateSeparator(entity: SeparatorEntity) {
        separatorDao.update(entity)
    }

    // Deletes a separator by its ID.
    suspend fun deleteSeparator(id: Int) {
        separatorDao.deleteById(id)
    }

    // Saves a new separator entity to the database.
    suspend fun saveSeparator(entity: SeparatorEntity): Long {
        return separatorDao.insert(entity)
    }

    // Retrieves all saved separator entities from the database.
    suspend fun getSavedSeparators() = separatorDao.getAll()


    // --- History Order Operations ---

    // Clears the existing history order and saves the new, updated order list.
    // This function ensures the displayed history order is persisted.
    suspend fun saveHistoryOrder(items: List<HistoryItemEntity>) {
        historyDao.clearAll() // Delete old entries
        historyDao.insertAll(items) // Insert new entries
    }

    // Retrieves the ordered list of history items (exercises and separators).
    suspend fun getHistoryOrder() = historyDao.getAllOrdered()


    // --- Utility Operations ---

    // Clears all records from all three local database tables (exercises, separators, and history order).
    suspend fun clearAll() {
        exerciseDao.clearAll()
        separatorDao.clearAll()
        historyDao.clearAll()
    }
}