package com.example.kelompokandori.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.kelompokandori.model.Destination
import DestinationRepository

class HomeViewModel : ViewModel() {
    private val repository = DestinationRepository()

    private val _destinations = MutableStateFlow<List<Destination>>(emptyList())
    val destinations = _destinations.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = repository.getDestinations()
            _destinations.value = data
            _isLoading.value = false
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}