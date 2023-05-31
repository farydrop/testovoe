package com.example.testovoe.domain.repository

import com.example.testovoe.domain.entity.UrlEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.Subject

interface UrlRepository {

    fun updateUrlSubject(): Subject<Unit>

    fun getUrl(): Single<UrlEntity>

    fun addUrl(url: UrlEntity): Completable

}