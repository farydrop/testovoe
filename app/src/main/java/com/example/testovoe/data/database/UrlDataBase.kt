package com.example.testovoe.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Url::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun urlDao(): UrlDao
}