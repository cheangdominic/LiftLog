package com.example.liftlog.api
import com.example.liftlog.BuildConfig

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {

    private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val headerInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .addHeader("X-RapidAPI-Key", BuildConfig.EXERCISEDB_API_KEY)
            .addHeader("X-RapidAPI-Host", BuildConfig.EXERCISEDB_API_HOST)
            .build()
        chain.proceed(req)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(headerInterceptor)
        .build()

    private val gson = GsonBuilder().create()

    val api: ExerciseApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ExerciseApi::class.java)
}
