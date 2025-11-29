package com.example.liftlog.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseEntity::class, SeparatorEntity::class, HistoryItemEntity::class], version = 4)
abstract class ExerciseDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun separatorDao(): SeparatorDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: ExerciseDatabase? = null

        fun getInstance(context: Context): ExerciseDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                                context,
                                ExerciseDatabase::class.java,
                                "exercise_db"
                            ).fallbackToDestructiveMigration(false)
                    .build().also { INSTANCE = it }
            }
        }
    }
}