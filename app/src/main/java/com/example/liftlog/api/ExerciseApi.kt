package com.example.liftlog.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

// API service class responsible for defining and executing specific Ktor network requests.
// It uses the configured HttpClient provided by ApiModule.
class ExerciseApi(private val client: HttpClient) {

    // Suspended function to fetch all exercises from the API endpoint "/exercises".
    // This uses the client configured with the BASE_URL and authentication headers.
    suspend fun getAllExercises(): List<ExerciseDto> {
        // client.get("exercises") performs a GET request relative to the base URL.
        // .body<List<ExerciseDto>>() automatically deserializes the JSON response body
        // into a List of ExerciseDto objects using the ContentNegotiation plugin.
        return client.get("exercises").body<List<ExerciseDto>>()
    }
}