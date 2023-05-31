package com.example.testovoe.domain.interactor

import com.example.testovoe.domain.entity.UrlEntity
import com.example.testovoe.domain.repository.UrlRepository
import com.example.testovoe.domain.usecase.StartUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class StartInteractor(private val urlRepository: UrlRepository): StartUseCase {
    override fun urlSubject(url: UrlEntity): Observable<UrlEntity> =
        urlRepository
            .updateUrlSubject()
            .observeOn(Schedulers.io())
            .flatMap {
                urlRepository
                    .getUrl()
                    .map { it }
                    .toObservable()
            }

    override fun insert(url: UrlEntity): Observable<UrlEntity> =
        urlRepository
            .addUrl(url)
            .doOnComplete{urlRepository.updateUrlSubject().onNext(Unit)}
            .toObservable()


}