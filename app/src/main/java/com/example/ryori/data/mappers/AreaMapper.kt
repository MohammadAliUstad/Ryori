package com.example.ryori.data.mappers

import com.example.ryori.data.model.domain.DomainArea
import com.example.ryori.data.model.domain.DomainAreas
import com.example.ryori.data.model.remote.RemoteArea
import com.example.ryori.data.model.remote.RemoteAreas

fun RemoteAreas.toDomainAreas(): DomainAreas {
    return DomainAreas(
        domainAreas = meals?.mapNotNull { it.toDomainArea() } ?: emptyList()
    )
}

fun RemoteArea.toDomainArea(): DomainArea {
    return DomainArea(
        name = strArea
    )
}