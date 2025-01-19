package com.yugentech.ryori.data.mappers

import com.yugentech.ryori.data.model.domain.DomainArea
import com.yugentech.ryori.data.model.domain.DomainAreas
import com.yugentech.ryori.data.model.remote.RemoteArea
import com.yugentech.ryori.data.model.remote.RemoteAreas

fun RemoteAreas.toDomainAreas(): DomainAreas {
    return DomainAreas(
        domainAreas = meals?.map { it.toDomainArea() } ?: emptyList()
    )
}

fun RemoteArea.toDomainArea(): DomainArea {
    return DomainArea(
        name = strArea
    )
}