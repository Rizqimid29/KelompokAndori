package com.example.kelompokandori.model

import kotlinx.serialization.Serializable

@Serializable
data class ReviewInsert(
    val overall_rating: Float,
    val kebersihan: Int,
    val pelayanan: Int,
    val lokasi: Int,
    val kenyamanan: Int,
    val description: String,
    val trip_type: String?,
    val media_url: String?,
    val media_type: String?,
    val user_id: String?
)
