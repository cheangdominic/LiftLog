package com.example.liftlog.state

import androidx.compose.runtime.mutableStateListOf
import com.example.liftlog.api.ExerciseDto
import com.example.liftlog.data.ExerciseRepository
import com.example.liftlog.data.room.ExerciseEntity
import com.example.liftlog.data.room.SeparatorEntity
import com.example.liftlog.data.room.HistoryItemEntity

class ExerciseStateHolder(private val repo: ExerciseRepository) {

    val apiExercises = mutableStateListOf<ExerciseDto>()
    private val allSeparators = mutableStateListOf<SeparatorEntity>()
    private val allSavedExercises = mutableStateListOf<ExerciseEntity>()
    val historyItems = mutableStateListOf<HistoryItem>()

    suspend fun loadApiExercises() {
        apiExercises.clear()
        apiExercises.addAll(repo.fetchExercises())
    }

    suspend fun loadSavedData() {
        allSavedExercises.clear()
        allSavedExercises.addAll(repo.getSavedExercises())

        allSeparators.clear()
        allSeparators.addAll(repo.getSavedSeparators())

        rebuildHistoryFromOrder()
    }

    suspend fun saveExercise(
        name: String,
        muscle: String?,
        sets: Int?,
        reps: Int?,
        weight: Double?
    ) {
        val dummyEx = ExerciseEntity(
            exerciseName = name,
            muscle = muscle,
            sets = sets,
            reps = reps,
            weight = weight
        )

        val newId = repo.saveExercise(dummyEx)

        val newEx = dummyEx.copy(id = newId)

        addExercise(newEx)
    }

    suspend fun updateExercise(
        id: Int,
        name: String,
        muscle: String?,
        sets: Int?,
        reps: Int?,
        weight: Double?
    ) {
        val updatedEx = ExerciseEntity(
            id = id,
            exerciseName = name,
            muscle = muscle,
            sets = sets,
            reps = reps,
            weight = weight
        )

        repo.updateExercise(updatedEx)

        val indexInSaved = allSavedExercises.indexOfFirst { it.id == id }
        if (indexInSaved != -1) {
            allSavedExercises[indexInSaved] = updatedEx
        }

        val indexInHistory = historyItems.indexOfFirst { it is HistoryItem.Exercise && it.ex.id == id }
        if (indexInHistory != -1) {
            historyItems[indexInHistory] = HistoryItem.Exercise(updatedEx, isNew = false)
        }
    }

    suspend fun deleteExercise(exerciseId: Int) {
        repo.deleteExercise(exerciseId)

        allSavedExercises.removeAll { it.id == exerciseId }

        historyItems.removeAll { it is HistoryItem.Exercise && it.ex.id == exerciseId }

        saveHistoryOrder()
    }

    suspend fun addSeparator(text: String) {
        val newSeparatorEntity = SeparatorEntity(text = text)
        repo.saveSeparator(newSeparatorEntity)

        allSeparators.clear()
        allSeparators.addAll(repo.getSavedSeparators())

        val insertIndex = 0

        val latestSeparator = allSeparators.firstOrNull { it.text == text }
        latestSeparator?.let {
            historyItems.add(insertIndex, HistoryItem.Separator(it.text, it.id))
            saveHistoryOrder()
        }
    }

    suspend fun addExercise(exercise: ExerciseEntity) {
        val insertIndex = 0

        val exerciseCopy = exercise.copy()
        allSavedExercises.add(0, exerciseCopy)
        historyItems.add(insertIndex, HistoryItem.Exercise(exerciseCopy))

        saveHistoryOrder()
    }

    suspend fun clearAll() {
        repo.clearAll()

        allSavedExercises.clear()
        allSeparators.clear()
        historyItems.clear()
    }

    private suspend fun saveHistoryOrder() {
        val entities = historyItems.mapIndexed { index, item ->
            when (item) {
                is HistoryItem.Exercise -> HistoryItemEntity(
                    type = "EXERCISE",
                    itemId = item.ex.id,
                    position = index
                )
                is HistoryItem.Separator -> HistoryItemEntity(
                    type = "SEPARATOR",
                    itemId = item.separatorId,
                    position = index
                )
            }
        }
        repo.saveHistoryOrder(entities)
    }

    suspend fun updateSeparator(separatorId: Int, newText: String) {
        val updatedSeparator = allSeparators.firstOrNull { it.id == separatorId }?.copy(text = newText)
        if (updatedSeparator != null) {
            repo.updateSeparator(updatedSeparator)

            val indexInAll = allSeparators.indexOfFirst { it.id == separatorId }
            if (indexInAll != -1) {
                allSeparators[indexInAll] = updatedSeparator
            }

            val indexInHistory = historyItems.indexOfFirst { it is HistoryItem.Separator && it.separatorId == separatorId }
            if (indexInHistory != -1) {
                historyItems[indexInHistory] = HistoryItem.Separator(newText, separatorId)
            }
        }
    }

    suspend fun deleteSeparator(separatorId: Int) {
        repo.deleteSeparator(separatorId)

        allSeparators.removeAll { it.id == separatorId }
        historyItems.removeAll { it is HistoryItem.Separator && it.separatorId == separatorId }

        saveHistoryOrder()
    }

    private suspend fun rebuildHistoryFromOrder() {
        historyItems.clear()
        val order = repo.getHistoryOrder()

        val exerciseMap = allSavedExercises.associateBy { it.id }
        val separatorMap = allSeparators.associateBy { it.id }

        order.forEach { entity ->
            when (entity.type) {
                "EXERCISE" -> {
                    exerciseMap[entity.itemId]?.let { ex ->
                        historyItems.add(HistoryItem.Exercise(ex, isNew = false))
                    }
                }
                "SEPARATOR" -> {
                    separatorMap[entity.itemId]?.let { sep ->
                        historyItems.add(HistoryItem.Separator(sep.text, sep.id))
                    }
                }
            }
        }
    }

    sealed class HistoryItem {
        data class Exercise(val ex: ExerciseEntity, var isNew: Boolean = true) : HistoryItem()
        data class Separator(val text: String, val separatorId: Int) : HistoryItem()
    }
}