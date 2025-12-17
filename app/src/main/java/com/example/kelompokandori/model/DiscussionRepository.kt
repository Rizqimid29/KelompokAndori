package com.example.kelompokandori.data

import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.model.DiscussionComment
import com.example.kelompokandori.model.Discussion
import com.example.kelompokandori.ui.home.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.Clock

class DiscussionRepository {

    private suspend fun getCurrentUserProfile(): UserProfile {
        val currentUser = SupabaseClient.client.auth.currentUserOrNull() ?: throw Exception("Login required")
        return SupabaseClient.client.from("profiles")
            .select { filter { eq("id", currentUser.id) } }
            .decodeSingle()
    }

    suspend fun getThreads(): List<Discussion> {
        return SupabaseClient.client.from("discussion_threads")
            .select { order("created_at", Order.DESCENDING) }
            .decodeList<Discussion>()
    }

    suspend fun createThread(title: String, content: String, imageBytes: ByteArray?) {
        val user = getCurrentUserProfile()

        var imageUrl: String? = null
        if (imageBytes != null) {
            val fileName = "thread_${Clock.System.now().toEpochMilliseconds()}.jpg"
            val bucket = SupabaseClient.client.storage.from("discussion-image")
            bucket.upload(fileName, imageBytes)
            imageUrl = bucket.publicUrl(fileName)
        }

        val thread = Discussion(
            userId = user.id,
            userName = user.fullName ?: "No Name",
            userAvatar = user.avatarUrl,
            title = title,
            content = content,
            imageUrl = imageUrl
        )
        SupabaseClient.client.from("discussion_threads").insert(thread)
    }

    suspend fun getComments(threadId: String): List<DiscussionComment> {
        return SupabaseClient.client.from("discussion_comments")
            .select {
                filter { eq("thread_id", threadId) }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<DiscussionComment>()
    }

    suspend fun postComment(
        threadId: String,
        content: String,
        parentId: String?,
        rootId: String?
    ) {
        val user = getCurrentUserProfile()

        val comment = DiscussionComment(
            threadId = threadId,
            parentId = parentId,
            rootId = rootId,
            userId = user.id,
            userName = user.fullName ?: "No Name",
            userAvatar = user.avatarUrl,
            content = content
        )
        SupabaseClient.client.from("discussion_comments").insert(comment)
    }
}