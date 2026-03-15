package com.juice.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stall(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    val latitude: Double,
    val longitude: Double,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val distance: Double? = null
)
