package com.example.liftlog.data

import com.example.liftlog.api.ExerciseApi
import com.example.liftlog.data.room.ExerciseDao
import com.example.liftlog.data.room.ExerciseEntity
import com.example.liftlog.data.room.SeparatorDao
import com.example.liftlog.data.room.SeparatorEntity
import com.example.liftlog.data.room.HistoryDao
import com.example.liftlog.data.room.HistoryItemEntity

class ExerciseRepository(
    private val api: ExerciseApi,
    private val exerciseDao: ExerciseDao,
    private val separatorDao: SeparatorDao,
    private val historyDao: HistoryDao
) {

    suspend fun fetchExercises() = api.getAllExercises()

    suspend fun saveExercise(exercise: ExerciseEntity): Int {
        val newId = exerciseDao.insert(exercise).toInt()
        return newId
    }

    suspend fun updateExercise(exercise: ExerciseEntity) {
        exerciseDao.update(exercise)
    }

    suspend fun updateSeparator(entity: SeparatorEntity) {
        separatorDao.update(entity)
    }

    suspend fun deleteSeparator(id: Int) {
        separatorDao.deleteById(id)
    }

    suspend fun deleteExercise(id: Int) {
        exerciseDao.deleteById(id)
    }

    suspend fun getSavedExercises() = exerciseDao.getAll()

    suspend fun saveSeparator(entity: SeparatorEntity) {
        separatorDao.insert(entity)
    }
    suspend fun getSavedSeparators() = separatorDao.getAll()

    suspend fun saveHistoryOrder(items: List<HistoryItemEntity>) {
        historyDao.clearAll()
        historyDao.insertAll(items)
    }

    suspend fun getHistoryOrder() = historyDao.getAllOrdered()

    suspend fun clearAll() {
        exerciseDao.clearAll()
        separatorDao.clearAll()
        historyDao.clearAll()
    }
}