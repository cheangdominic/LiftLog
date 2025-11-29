# 🏋️ LiftLog: Workout & History Tracker

**LiftLog** is a modern Android application designed to help users efficiently log their weightlifting exercises, track sets, reps, and weight, and organize their workout history using custom separators. It integrates with a public exercise API for discovering new exercises while maintaining robust local persistence using Room.

---

## ✨ Features

- **Manual Exercise Logging:** Quickly log exercises with details including **name, sets, reps, weight, and muscle group**.  
- **Workout History:** View a chronological feed of all logged exercises and custom separators.  
- **Custom Separators:** Insert date markers, titles (e.g., "Week 4", "Leg Day"), or notes to organize the workout feed.  
- **Edit & Delete:** Full CRUD (Create, Read, Update, Delete) functionality for both logged exercises and separators.  
- **Remote Exercise Discovery (Planned):** Integrates with the ExerciseDB API to fetch exercises and details, while local logging remains the core functionality.  
- **Modern Android Stack:** Built entirely using **Kotlin, Jetpack Compose, Coroutines, and MVVM principles**.

---

## 💻 Tech Stack

| **Component**       | **Technology**                  | **Description**                                                                 |
|--------------------|--------------------------------|-------------------------------------------------------------------------------|
| UI                  | Jetpack Compose                 | Declarative UI framework for a modern, responsive interface.                  |
| State Management    | MVVM & Kotlin `StateHolder`     | Handles business logic and maintains app state across the lifecycle.          |
| Persistence         | Room                            | Local SQL database for storing exercises, separators, and history order.      |
| Networking          | Ktor                            | Fetches exercise data from the external **ExerciseDB API**.                   |
| Concurrency         | Kotlin Coroutines               | Manages asynchronous operations for database and network tasks.               |
| Build System        | Gradle Kotlin DSL (.kts)        | Modern, type-safe build configuration.                                        |

---

## ⚙️ Setup & Installation

### Prerequisites

- **Android Studio** (Electric Eel / Hedgehog or newer recommended)  
- **JDK 17** (or newer)  
- **Android SDK 36**  

### API Key Configuration

This project requires an API key and host URL for the ExerciseDB service:

1. Sign up for an API key on a service providing exercise data (e.g., RapidAPI for ExerciseDB).  
2. In your `app/build.gradle.kts` file, define the following `buildConfigField` variables inside the `defaultConfig` block:

```kotlin
// app/build.gradle.kts (inside defaultConfig)
buildConfigField("String", "EXERCISEDB_API_KEY", "\"YOUR_API_KEY_HERE\"")
buildConfigField("String", "EXERCISEDB_API_HOST", "\"YOUR_API_HOST_HERE\"")
