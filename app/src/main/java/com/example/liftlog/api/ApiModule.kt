package com.example.liftlog.api

import com.example.liftlog.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor

// Singleton object responsible for configuring and providing the Ktor HttpClient instance.
object ApiModule {

    // Base URL for the ExerciseDB RapidAPI endpoint.
    private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    // The configured HttpClient instance using the OkHttp engine.
    val apiClient: HttpClient = HttpClient(OkHttp) {

        // Plugin to set configuration for every outgoing request.
        defaultRequest {
            url(BASE_URL) // Set the base URL for the request.
            // Add required headers for RapidAPI authorization.
            header("X-RapidAPI-Key", BuildConfig.EXERCISEDB_API_KEY)
            header("X-RapidAPI-Host", BuildConfig.EXERCISEDB_API_HOST)
        }

        // Plugin for content serialization/deserialization (JSON in this case).
        install(ContentNegotiation) {
            json(Json {
                // Configure Kotlinx Serialization JSON parser.
                prettyPrint = true // Format JSON output nicely (useful for logging/debugging).
                ignoreUnknownKeys = true // Ignore fields in JSON response not present in the data classes.
                isLenient = true // Allow for more relaxed parsing rules.
            })
        }

        // Plugin for request/response logging.
        install(Logging) {
            level = LogLevel.ALL // Log headers, body, and status for all requests.
            logger = object : Logger {
                // Custom logger implementation to pipe Ktor logs through OkHttp's standard logger.
                override fun log(message: String) {
                    HttpLoggingInterceptor.Logger.DEFAULT.log(message)
                }
            }
        }
    }
}