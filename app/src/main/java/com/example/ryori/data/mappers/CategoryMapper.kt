package com.example.ryori.data.mappers

import com.example.ryori.data.model.domain.DomainCategories
import com.example.ryori.data.model.domain.DomainCategory
import com.example.ryori.data.model.remote.RemoteCategories
import com.example.ryori.data.model.remote.RemoteCategory

fun RemoteCategories.toDomainCategories(): DomainCategories {
    return DomainCategories(
        domainCategories = categories?.mapNotNull { remoteCategory ->
            remoteCategory?.toDomainCategory()
        } ?: emptyList()
    )
}

fun RemoteCategory.toDomainCategory(): DomainCategory {
    return DomainCategory(
        name = strCategory,
        description = strCategoryDescription,
        image = strCategoryThumb
    )
}