package com.example.kelompokandori.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscussionComment(
    val id: String? = null,
    @SerialName("thread_id") val threadId: String,
    @SerialName("parent_id") val parentId: String? = null,
    @SerialName("root_id") val rootId: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String?,
    @SerialName("user_avatar") val userAvatar: String?,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null,

    val replies: List<DiscussionComment> = emptyList()
)