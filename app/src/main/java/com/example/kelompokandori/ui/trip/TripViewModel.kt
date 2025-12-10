package com.example.kelompokandori.ui.trip

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.model.Trip
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.util.UUID

class TripViewModel : ViewModel() {

    // List untuk menampung data trips (State)
    private val _trips = mutableStateListOf<Trip>()
    val trips: List<Trip> get() = _trips

    var isLoading = false
    var errorMessage = ""

    fun getTrips() {
        viewModelScope.launch {
            try {
                isLoading = true
                // Mengambil data dari tabel 'trips'
                val result = SupabaseClient.client.from("trips").select().decodeList<Trip>()
                _trips.clear()
                _trips.addAll(result)
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error loading trips"
            } finally {
                isLoading = false
            }
        }
    }

    fun addTrip(
        destination: String,
        startDate: String,
        endDate: String,
        description: String,
        userId: String,
        imageByteArray: ByteArray? // Data gambar dalam bentuk byte
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                var finalImageUrl: String? = null

                // 1. Upload Gambar ke Storage (jika ada)
                if (imageByteArray != null) {
                    val fileName = "trip_${UUID.randomUUID()}.jpg"
                    val bucket = SupabaseClient.client.storage.from("trip-images")
                    bucket.upload(fileName, imageByteArray)

                    // Dapatkan Public URL agar bisa ditampilkan di UI
                    finalImageUrl = bucket.publicUrl(fileName)
                }

                // 2. Simpan Data Trip ke Database
                val newTrip = Trip(
                    userId = userId,
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    description = description,
                    imageUrl = finalImageUrl
                )

                SupabaseClient.client.from("trips").insert(newTrip)

                // Refresh list
                getTrips()

            } catch (e: Exception) {
                errorMessage = e.message ?: "Gagal menyimpan trip"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}