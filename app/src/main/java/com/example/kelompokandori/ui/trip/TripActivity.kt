package com.example.kelompokandori.ui.trip

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kelompokandori.ui.theme.KelompokAndoriTheme

class TripActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KelompokAndoriTheme {
                val viewModel: TripViewModel = viewModel()
                val lifecycleOwner = LocalLifecycleOwner.current

                // Fitur Auto-Refresh: Ambil data ulang saat kembali ke halaman ini
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.getTrips()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                TripListScreen(
                    viewModel = viewModel,
                    onNavigateToAdd = {
                        // Pindah ke halaman Tambah Trip
                        startActivity(Intent(this, AddTripActivity::class.java))
                    }
                )
            }
        }
    }
}