package com.example.liftlog.navigation

// A sealed class defining the possible screens/destinations in the application.
// This structure helps ensure all navigation routes are known and type-safe.
sealed class Screen(val route: String) {
    // Screen for the main list of exercises (e.g., the Discovery screen).
    object Exercises : Screen("exercises")

    // Screen for displaying detailed information about a single exercise.
    // It uses a dynamic route parameter '{id}' for the exercise identifier.
    object Detail : Screen("detail/{id}") {
        // Helper function to create the actual route string by substituting the ID.
        fun createRoute(id: String) = "detail/$id"
    }

    // Screen for viewing the user's workout history.
    object History : Screen("history")
}