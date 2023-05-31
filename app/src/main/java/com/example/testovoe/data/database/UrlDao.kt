package com.example.testovoe.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.testovoe.domain.entity.UrlEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface UrlDao {
    @Query("SELECT * FROM url")
    fun getAll(): Single<Url>

    @Insert
    fun insert(url: Url): Completable
}