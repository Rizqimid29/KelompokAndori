package com.example.kelompokandori.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    val id: String? = null,
    val name: String,
    val description: String?,
    @SerialName("location_city") val locationCity: String?,
    @SerialName("cover_image_url") val coverImageUrl: String?
)