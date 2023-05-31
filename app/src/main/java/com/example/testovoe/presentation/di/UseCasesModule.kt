package com.example.testovoe.presentation.di

import com.example.testovoe.domain.interactor.StartInteractor
import com.example.testovoe.domain.usecase.StartUseCase
import org.koin.dsl.module

val useCasesModule = module {
    factory<StartUseCase> { StartInteractor(get()) }
}