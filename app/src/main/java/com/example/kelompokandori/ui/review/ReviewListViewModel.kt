package com.example.kelompokandori.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.model.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewListViewModel : ViewModel() {

    private val repository = ReviewRepository()

    private val _uiState = MutableStateFlow(ReviewListUiState())
    val uiState: StateFlow<ReviewListUiState> = _uiState

    init {
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val reviews = repository.getAllReviews()
                _uiState.update {
                    it.copy(
                        reviews = reviews,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memuat review"
                    )
                }
            }
        }
    }
}
