package com.example.kelompokandori

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    const val SUPABASE_URL = "https://supabase.com/dashboard/project/malicufowlmvuckjthmk"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1hbGljdWZvd2xtdnVja2p0aG1rIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ5MDQxMTYsImV4cCI6MjA4MDQ4MDExNn0.LayhV_nQrIQ7DdLs5BHUCG-kmTk1Mr7B3Nt4bkhFY2Y"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest) // Untuk database
        install(Auth)      // Untuk login
    }
}