package com.example.kelompokandori.ui.trip

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.model.Trip
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.util.UUID

class TripViewModel : ViewModel() {

    private val _trips = mutableStateListOf<Trip>()
    val trips: List<Trip> get() = _trips

    var isLoading = false
    var errorMessage = ""

    fun getTrips() {
        viewModelScope.launch {
            try {
                isLoading = true
                val result = SupabaseClient.client.from("trips").select().decodeList<Trip>()
                _trips.clear()
                _trips.addAll(result)
            } catch (e: Exception) {
                errorMessage = "Gagal ambil data: ${e.message}"
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
        imageByteArray: ByteArray?
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                var finalImageUrl: String? = null

                if (imageByteArray != null) {
                    val fileName = "trip_${UUID.randomUUID()}.jpg"
                    val bucket = SupabaseClient.client.storage.from("trip-images")
                    bucket.upload(fileName, imageByteArray)
                    finalImageUrl = bucket.publicUrl(fileName)
                }

                val newTrip = Trip(
                    userId = userId,
                    destination = destination,
                    startDate = startDate,
                    endDate = endDate,
                    description = description,
                    imageUrl = finalImageUrl
                )

                SupabaseClient.client.from("trips").insert(newTrip)
                getTrips()

            } catch (e: Exception) {
                errorMessage = "Gagal simpan: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}