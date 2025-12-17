package com.example.kelompokandori.ui.trip

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kelompokandori.model.Trip

@Composable
fun TripListScreen(
    viewModel: TripViewModel = viewModel(),
    onNavigateToAdd: (Trip?) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getTrips()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAdd(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                text = "My Trips",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (viewModel.errorMessage.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            LazyColumn {
                items(viewModel.trips) { trip ->
                    TripCard(
                        trip = trip,
                        onEditClick = { selectedTrip ->
                            onNavigateToAdd(selectedTrip)
                        },
                        onDeleteClick = { selectedTrip ->
                        }
                    )
                }
            }
        }
    }
}