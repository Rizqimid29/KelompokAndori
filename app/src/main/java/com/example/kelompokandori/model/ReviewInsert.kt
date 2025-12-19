package com.example.kelompokandori.model

import kotlinx.serialization.Serializable

@Serializable
data class ReviewInsert(
    val user_id: String,
    val overall_rating: Float,
    val kebersihan: Int,
    val pelayanan: Int,
    val lokasi: Int,
    val kenyamanan: Int,
    val description: String,
    val trip_type: String? = null,
    val media_url: String? = null,
    val media_type: String? = null
)
