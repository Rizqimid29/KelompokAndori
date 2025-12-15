package com.example.kelompokandori.ui.review

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddReviewScreen() {

    val application = LocalContext.current.applicationContext as Application

    val viewModel: AddReviewViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddReviewViewModel(application) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.setMedia(it, MediaType.IMAGE)
        }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Review Penginapan", style = MaterialTheme.typography.headlineSmall)

        RatingRow("Kebersihan", state.kebersihan) {
            viewModel.setRating(RatingType.KEBERSIHAN, it)
        }

        RatingRow("Pelayanan", state.pelayanan) {
            viewModel.setRating(RatingType.PELAYANAN, it)
        }

        RatingRow("Lokasi", state.lokasi) {
            viewModel.setRating(RatingType.LOKASI, it)
        }

        RatingRow("Kenyamanan", state.kenyamanan) {
            viewModel.setRating(RatingType.KENYAMANAN, it)
        }

        val overall =
            (state.kebersihan + state.pelayanan +
                    state.lokasi + state.kenyamanan) / 4f

        Text("Rating Keseluruhan: ${"%.1f".format(overall)} ⭐")

        OutlinedTextField(
            value = state.pengalaman,
            onValueChange = viewModel::setPengalaman,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ceritakan pengalaman Anda…") }
        )

        OutlinedButton(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Foto")
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state.isSuccess) {
            Text(
                text = "Review berhasil dikirim 🎉",
                color = Color(0xFF9AA9E3)

            )
        }

        Button(
            onClick = viewModel::submitReview,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Posting Review")
            }
        }
    }
}

@Composable
fun RatingRow(
    title: String,
    rating: Int,
    onRate: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, modifier = Modifier.weight(1f))

        Row {
            for (i in 1..5) {
                val isSelected = i <= rating

                Icon(
                    imageVector = if (isSelected)
                        Icons.Filled.Star
                    else
                        Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (isSelected)
                        Color(0xFFFFC107) // ⭐ kuning
                    else
                        Color.Gray,      // ☆ abu-abu
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onRate(i) }
                )
            }
        }
    }
}

