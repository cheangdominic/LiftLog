package com.example.liftlog.api

import kotlinx.serialization.Serializable

// Data Transfer Object (DTO) representing an exercise fetched from the external API.
// @Serializable annotation is required for Kotlinx Serialization to automatically parse/serialize JSON data.
@Serializable
data class ExerciseDto(
    // Unique identifier for the exercise from the API.
    val id: String,
    // The official name of the exercise.
    val name: String,
    // The main body part targeted by the exercise (nullable).
    val bodyPart: String?,
    // The specific muscle targeted by the exercise (nullable).
    val target: String?,
    // Internal flag used in the state management (e.g., to indicate recent addition in the UI).
    var isNew: Boolean = true
)