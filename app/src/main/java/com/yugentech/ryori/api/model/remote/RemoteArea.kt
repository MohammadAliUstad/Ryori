package com.yugentech.ryori.api.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class RemoteAreas(
    val meals: List<RemoteArea>? = null
)

@Serializable
data class RemoteArea(
    val strArea: String? = null
)
