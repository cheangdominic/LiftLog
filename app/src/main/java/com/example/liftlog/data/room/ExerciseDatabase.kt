package com.example.liftlog.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Defines the Room database structure.
// entities: Lists all data classes that are tables in this database.
// version: Must be incremented whenever the schema changes.
@Database(entities = [ExerciseEntity::class, SeparatorEntity::class, HistoryItemEntity::class], version = 9)
abstract class ExerciseDatabase : RoomDatabase() {

    // Abstract function to expose the Data Access Object for Exercise logs.
    abstract fun exerciseDao(): ExerciseDao
    // Abstract function to expose the DAO for Separator entities.
    abstract fun separatorDao(): SeparatorDao
    // Abstract function to expose the DAO for history item order.
    abstract fun historyDao(): HistoryDao

    // Singleton pattern implementation to ensure only one instance of the database is created.
    companion object {
        // Marks the INSTANCE as immediately visible to other threads.
        @Volatile private var INSTANCE: ExerciseDatabase? = null

        // Provides a thread-safe way to get the single database instance.
        fun getInstance(context: Context): ExerciseDatabase {
            return INSTANCE ?: synchronized(this) {
                // If INSTANCE is null, create the database.
                Room.databaseBuilder(
                    context,
                    ExerciseDatabase::class.java,
                    "exercise_db" // The name of the database file on disk.
                )
                    // Allows Room to rebuild tables when version changes without a migration path.
                    // Set to false to prevent accidental destructive migration.
                    .fallbackToDestructiveMigration(false)
                    .build().also { INSTANCE = it } // Assign the built instance to INSTANCE.
            }
        }
    }
}