package com.example.kelompokandori.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trip(
    val id: Long? = null,

    @SerialName("user_id")
    val userId: String,

    val destination: String,

    @SerialName("start_date")
    val startDate: String,

    @SerialName("end_date")
    val endDate: String,

    val description: String,

    @SerialName("image_url")
    val imageUrl: String? = null
)