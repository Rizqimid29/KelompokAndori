package com.example.kelompokandori.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Discussion(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String?,
    @SerialName("user_avatar") val userAvatar: String?,
    val title: String,
    val content: String,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("created_at") val createdAt: String? = null
)