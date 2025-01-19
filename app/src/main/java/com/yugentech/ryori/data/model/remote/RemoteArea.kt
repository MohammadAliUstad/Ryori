package com.yugentech.ryori.data.model.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteAreas(
    val meals: List<RemoteArea>?
)

@JsonClass(generateAdapter = true)
data class RemoteArea(
    val strArea: String?
)