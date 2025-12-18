package com.example.kelompokandori.model

import com.example.kelompokandori.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class ReviewRepository {

    private val client = SupabaseClient.client

    suspend fun getAllReviews(): List<Review> {
        return client
            .postgrest["reviews"]
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList()
    }
}
