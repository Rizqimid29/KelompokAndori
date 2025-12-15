package com.example.kelompokandori.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleComment(
    val id: Long? = null,
    val content: String,

    @SerialName("article_id")
    val articleId: Long,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("user_name")
    val userName: String? = "Anonymous",

    @SerialName("created_at")
    val createdAt: String? = null
)