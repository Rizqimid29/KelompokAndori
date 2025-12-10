package com.example.kelompokandori.ui.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TripListScreen(
    viewModel: TripViewModel = viewModel(),
    onNavigateToAdd: () -> Unit
) {
    // Panggil data saat pertama kali dibuka
    LaunchedEffect(Unit) {
        viewModel.getTrips()
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) { // <--- Panggil di sini
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Background abu-abu muda
                .padding(16.dp)
        ) {

            Text(
                text = "My Trips",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tampilkan Loading Indicator jika sedang memuat data
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // LAZY LIST untuk menampilkan daftar trip
            LazyColumn {
                items(viewModel.trips) { trip ->
                    // Memanggil komponen kartu yang sudah dibuat di TripItem.kt
                    // Karena berada di package yang sama, tidak perlu di-import manual
                    TripCard(trip = trip)
                }
            }
        }
    }
}