package com.example.kelompokandori.model

import android.content.Context
import android.net.Uri
import com.example.kelompokandori.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

class AddReviewRepository(
    private val context: Context
) {

    private val client = SupabaseClient.client

    suspend fun addReview(
        kebersihan: Int,
        pelayanan: Int,
        lokasi: Int,
        kenyamanan: Int,
        pengalaman: String,
        tipeTrip: String?,
        mediaUri: Uri?,
        mediaType: String?
    ): String? {   // 🔥 WAJIB RETURN

        val userId = client.auth.currentUserOrNull()?.id
            ?: throw Exception("User belum login")

        val overallRating =
            (kebersihan + pelayanan + lokasi + kenyamanan) / 4f

        var mediaUrl: String? = null

        if (mediaUri != null && mediaType != null) {

            val bytes = context.contentResolver
                .openInputStream(mediaUri)
                ?.readBytes()
                ?: throw Exception("Gagal membaca file")

            val ext = if (mediaType == "VIDEO") "mp4" else "jpg"
            val path = "reviews/${System.currentTimeMillis()}.$ext"

            client.storage
                .from("review-media")
                .upload(path, bytes)

            mediaUrl = client.storage
                .from("review-media")
                .publicUrl(path)
        }

        val review = ReviewInsert(
            user_id = userId,
            overall_rating = overallRating,
            kebersihan = kebersihan,
            pelayanan = pelayanan,
            lokasi = lokasi,
            kenyamanan = kenyamanan,
            description = pengalaman,
            trip_type = tipeTrip,
            media_url = mediaUrl,
            media_type = mediaType
        )

        client.postgrest["reviews"].insert(review)


        return mediaUrl
    }
}
