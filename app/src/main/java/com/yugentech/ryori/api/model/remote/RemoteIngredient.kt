package com.yugentech.ryori.api.model.remote

import kotlinx.serialization.Serializable

// TheMealDB list.php?i=list
@Serializable
data class RemoteIngredients(
    val meals: List<RemoteIngredient?>? = null
)

@Serializable
data class RemoteIngredient(
    val idIngredient: String? = null,
    val strIngredient: String? = null,
    val strDescription: String? = null,
    val strType: String? = null
)
