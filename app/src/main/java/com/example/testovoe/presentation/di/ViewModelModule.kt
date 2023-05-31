package com.example.testovoe.presentation.di

import com.example.testovoe.presentation.viewmodel.StartViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { StartViewModel(get()) }
}