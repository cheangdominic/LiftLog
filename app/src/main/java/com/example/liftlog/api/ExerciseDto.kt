package com.example.liftlog.api

data class ExerciseDto(
    val id: String,
    val name: String,
    val bodyPart: String?,
    val target: String?,
    var isNew: Boolean = true
)
