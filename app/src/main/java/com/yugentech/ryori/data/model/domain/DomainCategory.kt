package com.yugentech.ryori.data.model.domain

data class DomainCategories(
    val domainCategories: List<DomainCategory>
)

data class DomainCategory(
    val name: String?,
    val description: String?,
    val image: String?
)