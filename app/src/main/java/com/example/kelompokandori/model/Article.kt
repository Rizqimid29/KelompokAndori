package com.example.kelompokandori.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val id: Long? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    val title: String,
    val content: String,

    @SerialName("user_id")
    val userId: String
)