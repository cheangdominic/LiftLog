package com.example.liftlog.state

import androidx.compose.runtime.mutableStateListOf
import com.example.liftlog.api.ExerciseDto
import com.example.liftlog.data.ExerciseRepository
import com.example.liftlog.data.room.ExerciseEntity
import com.example.liftlog.data.room.SeparatorEntity
import com.example.liftlog.data.room.HistoryItemEntity

// Manages all exercise-related state, including API data, saved logs, and history order.
class ExerciseStateHolder(private val repo: ExerciseRepository) {

    // List of exercises fetched from the external API (Discovery screen).
    val apiExercises = mutableStateListOf<ExerciseDto>()
    // Internal lists holding the raw data retrieved from the local database.
    private val allSeparators = mutableStateListOf<SeparatorEntity>()
    private val allSavedExercises = mutableStateListOf<ExerciseEntity>()
    // The combined, ordered list of exercises and separators displayed on the History screen.
    val historyItems = mutableStateListOf<HistoryItem>()

    // Fetches exercises from the API and updates the state list.
    suspend fun loadApiExercises() {
        apiExercises.clear()
        apiExercises.addAll(repo.fetchExercises())
    }

    // Loads all saved data (exercises, separators, and their order) from the database.
    suspend fun loadSavedData() {
        allSavedExercises.clear()
        allSavedExercises.addAll(repo.getSavedExercises())

        allSeparators.clear()
        allSeparators.addAll(repo.getSavedSeparators())

        // Rebuild the visible history list based on the loaded order.
        rebuildHistoryFromOrder()
    }

    // Creates a new ExerciseEntity, saves it, and adds it to the history list.
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

        // Save to DB and get the newly generated ID.
        val newId = repo.saveExercise(dummyEx)

        // Create the final ExerciseEntity with the correct ID.
        val newEx = dummyEx.copy(id = newId)

        // Add the new exercise to the in-memory state and history.
        addExercise(newEx)
    }

    // Updates an existing ExerciseEntity in the database and in the state lists.
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

        // Update in the database.
        repo.updateExercise(updatedEx)

        // Update in the raw saved exercises list.
        val indexInSaved = allSavedExercises.indexOfFirst { it.id == id }
        if (indexInSaved != -1) {
            allSavedExercises[indexInSaved] = updatedEx
        }

        // Update in the visible history list, triggering recomposition.
        val indexInHistory = historyItems.indexOfFirst { it is HistoryItem.Exercise && it.ex.id == id }
        if (indexInHistory != -1) {
            historyItems[indexInHistory] = HistoryItem.Exercise(updatedEx, isNew = false)
        }
    }

    // Deletes an exercise from the database and removes it from all state lists.
    suspend fun deleteExercise(exerciseId: Int) {
        repo.deleteExercise(exerciseId)

        allSavedExercises.removeAll { it.id == exerciseId }

        historyItems.removeAll { it is HistoryItem.Exercise && it.ex.id == exerciseId }

        // Save the updated order to the database.
        saveHistoryOrder()
    }

    // Creates a new SeparatorEntity, saves it, and adds it to the beginning of the history.
    suspend fun addSeparator(text: String) {
        // Save to DB
        val newId = repo.saveSeparator(SeparatorEntity(text = text)).toInt()

        // Add to top of history list
        historyItems.add(0, HistoryItem.Separator(text, newId))
        // Add to local separator list
        allSeparators.add(SeparatorEntity(id = newId, text = text))
        // Persist new history order
        saveHistoryOrder()
    }


    // Adds a newly saved exercise to the beginning of the history list.
    suspend fun addExercise(exercise: ExerciseEntity) {
        val insertIndex = 0

        // Add a copy to prevent mutation issues with the history item wrapper.
        val exerciseCopy = exercise.copy()
        // Add to the raw list.
        allSavedExercises.add(0, exerciseCopy)
        // Add to the top of the visible history list.
        historyItems.add(insertIndex, HistoryItem.Exercise(exerciseCopy))

        // Save the new history order.
        saveHistoryOrder()
    }

    // Clears all data from the database and all state lists.
    suspend fun clearAll() {
        repo.clearAll()

        allSavedExercises.clear()
        allSeparators.clear()
        historyItems.clear()
    }

    // Persists the current order of items in 'historyItems' to the database.
    private suspend fun saveHistoryOrder() {
        // Map the combined history list into storable HistoryItemEntity objects.
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

    // Updates the text of an existing separator in the database and in the state lists.
    suspend fun updateSeparator(separatorId: Int, newText: String) {
        val updatedSeparator = allSeparators.firstOrNull { it.id == separatorId }?.copy(text = newText)
        if (updatedSeparator != null) {
            // Update in the database.
            repo.updateSeparator(updatedSeparator)

            // Update in the raw saved separators list.
            val indexInAll = allSeparators.indexOfFirst { it.id == separatorId }
            if (indexInAll != -1) {
                allSeparators[indexInAll] = updatedSeparator
            }

            // Update in the visible history list, triggering recomposition.
            val indexInHistory = historyItems.indexOfFirst { it is HistoryItem.Separator && it.separatorId == separatorId }
            if (indexInHistory != -1) {
                historyItems[indexInHistory] = HistoryItem.Separator(newText, separatorId)
            }
        }
    }

    // Deletes a separator from the database and removes it from all state lists.
    suspend fun deleteSeparator(separatorId: Int) {
        repo.deleteSeparator(separatorId)

        allSeparators.removeAll { it.id == separatorId }
        historyItems.removeAll { it is HistoryItem.Separator && it.separatorId == separatorId }

        // Save the updated order to the database.
        saveHistoryOrder()
    }

    // Rebuilds the combined history list using the persisted order and available data.
    private suspend fun rebuildHistoryFromOrder() {
        historyItems.clear()
        val order = repo.getHistoryOrder()

        // Create fast lookup maps for exercises and separators.
        val exerciseMap = allSavedExercises.associateBy { it.id }
        val separatorMap = allSeparators.associateBy { it.id }

        // Iterate through the saved order and populate the historyItems list.
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

    // Sealed class representing an item in the combined history list.
    sealed class HistoryItem {
        // Wrapper for a logged exercise. 'isNew' flag indicates recent addition (optional for UI effects).
        data class Exercise(val ex: ExerciseEntity, var isNew: Boolean = true) : HistoryItem()
        // Wrapper for a user-defined separator/break.
        data class Separator(val text: String, val separatorId: Int) : HistoryItem()
    }
}