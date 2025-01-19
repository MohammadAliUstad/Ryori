package com.yugentech.ryori.data.model.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteCategories(
    val categories: List<RemoteCategory?>?
)

@JsonClass(generateAdapter = true)
data class RemoteCategory(
    val idCategory: String?,
    val strCategory: String?,
    val strCategoryDescription: String?,
    val strCategoryThumb: String?
)