package com.example.testovoe.data

import com.example.testovoe.data.database.UrlDao
import com.example.testovoe.data.database.UrlEntityMapper
import com.example.testovoe.data.database.UrlMapper
import com.example.testovoe.domain.entity.UrlEntity
import com.example.testovoe.domain.repository.UrlRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

class UrlRepositoryImpl(private val urlDao: UrlDao) : UrlRepository {

    private val updateUrlSubject = BehaviorSubject.createDefault(Unit)
    private val urlEntityMapper by lazy { UrlEntityMapper() }
    private val urlMapper by lazy { UrlMapper() }

    override fun updateUrlSubject(): Subject<Unit> = updateUrlSubject
    override fun getUrl(): Single<UrlEntity> =
        urlDao.getAll()
            .map { urlEntityMapper.apply(it) }

    override fun addUrl(url: UrlEntity): Completable =
        urlDao.insert(urlMapper.apply(url))
}