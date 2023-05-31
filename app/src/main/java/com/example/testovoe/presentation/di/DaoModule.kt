package com.example.testovoe.presentation.di

import android.app.Application
import androidx.room.Room
import com.example.testovoe.data.database.AppDatabase
import com.example.testovoe.data.database.UrlDao
import org.koin.dsl.module

val daoModule = module {
    single { createDataBase(get()) }
    single { createUrlDao(get()) }
}

private fun createDataBase(app: Application): AppDatabase =
    Room
        .databaseBuilder(app, AppDatabase::class.java, "AppDatabase")
        .build()

private fun createUrlDao(database: AppDatabase): UrlDao =
    database.urlDao()