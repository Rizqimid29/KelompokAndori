package com.example.kelompokandori.ui.review

import com.example.kelompokandori.model.Review

data class ReviewListUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
