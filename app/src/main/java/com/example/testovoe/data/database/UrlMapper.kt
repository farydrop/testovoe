package com.example.testovoe.data.database

import com.example.testovoe.domain.entity.UrlEntity
import io.reactivex.rxjava3.functions.Function

class UrlEntityMapper : Function<Url,UrlEntity> {
    override fun apply(urlModel: Url): UrlEntity =
        UrlEntity(
            id = urlModel.id,
            url = urlModel.name
        )
}

class UrlMapper : Function<UrlEntity, Url> {
    override fun apply(info: UrlEntity): Url =
        Url(
            id = info.id,
            name = info.url
        )
}