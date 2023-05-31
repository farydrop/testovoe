package com.example.testovoe.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.testovoe.presentation.common.SingleLiveDataEmpty
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {
    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}