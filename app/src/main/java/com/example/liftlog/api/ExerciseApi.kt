package com.example.liftlog.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ExerciseApi(private val client: HttpClient) {

    suspend fun getAllExercises(): List<ExerciseDto> {
        return client.get("exercises").body<List<ExerciseDto>>()
    }
}