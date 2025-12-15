package com.example.kelompokandori.ui.review

import android.net.Uri

data class AddReviewUiState(
    val kebersihan: Int = 0,
    val pelayanan: Int = 0,
    val lokasi: Int = 0,
    val kenyamanan: Int = 0,
    val pengalaman: String = "",
    val tipeTrip: String? = null,
    val mediaUri: Uri? = null,
    val mediaType: MediaType? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
