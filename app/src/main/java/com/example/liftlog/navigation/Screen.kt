package com.example.liftlog.navigation

sealed class Screen(val route: String) {
    object Exercises : Screen("exercises")
    object Detail : Screen("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
    object History : Screen("history")
}
