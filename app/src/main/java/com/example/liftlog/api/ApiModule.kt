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

object ApiModule {

    private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    val apiClient: HttpClient = HttpClient(OkHttp) {

        defaultRequest {
            url(BASE_URL)
            header("X-RapidAPI-Key", BuildConfig.EXERCISEDB_API_KEY)
            header("X-RapidAPI-Host", BuildConfig.EXERCISEDB_API_HOST)
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    HttpLoggingInterceptor.Logger.DEFAULT.log(message)
                }
            }
        }

        engine {
            config {

            }
        }
    }
}