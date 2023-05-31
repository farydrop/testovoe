package com.example.testovoe.presentation.di

import com.example.testovoe.data.UrlRepositoryImpl
import com.example.testovoe.domain.repository.UrlRepository
import org.koin.dsl.module

val repositoryModule = module {
    single <UrlRepository> { UrlRepositoryImpl(get()) }
}