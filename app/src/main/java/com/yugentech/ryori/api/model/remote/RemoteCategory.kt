package com.yugentech.ryori.api.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class RemoteCategories(
    val categories: List<RemoteCategory?>? = null
)

@Serializable
data class RemoteCategory(
    val idCategory: String? = null,
    val strCategory: String? = null,
    val strCategoryDescription: String? = null,
    val strCategoryThumb: String? = null
)