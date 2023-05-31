package com.example.testovoe.domain.usecase

import com.example.testovoe.domain.entity.UrlEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface StartUseCase {

    fun urlSubject(url: UrlEntity):Observable<UrlEntity>

    fun insert(url: UrlEntity): Observable<UrlEntity>

}

/*
sealed class StartActionState {
    object Start: StartActionState()
    object Success: StartActionState()

    data class Error(val error: Throwable) : StartActionState()
}*/
