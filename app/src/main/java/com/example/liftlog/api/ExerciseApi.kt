package com.example.liftlog.api

import retrofit2.http.GET

interface ExerciseApi {

    @GET("exercises")
    suspend fun getAllExercises(): List<ExerciseDto>
}
