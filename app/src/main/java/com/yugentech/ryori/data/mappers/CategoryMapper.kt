package com.yugentech.ryori.data.mappers

import com.yugentech.ryori.data.model.domain.DomainCategories
import com.yugentech.ryori.data.model.domain.DomainCategory
import com.yugentech.ryori.data.model.remote.RemoteCategories
import com.yugentech.ryori.data.model.remote.RemoteCategory

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