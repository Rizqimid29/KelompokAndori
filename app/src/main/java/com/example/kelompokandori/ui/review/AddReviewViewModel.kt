package com.example.kelompokandori.ui.review

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.model.AddReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddReviewViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = AddReviewRepository(application)

    private val _uiState = MutableStateFlow(AddReviewUiState())
    val uiState: StateFlow<AddReviewUiState> = _uiState

    fun setRating(type: RatingType, value: Int) {
        _uiState.update {
            when (type) {
                RatingType.KEBERSIHAN -> it.copy(kebersihan = value)
                RatingType.PELAYANAN -> it.copy(pelayanan = value)
                RatingType.LOKASI -> it.copy(lokasi = value)
                RatingType.KENYAMANAN -> it.copy(kenyamanan = value)
            }
        }
    }

    fun setPengalaman(value: String) {
        _uiState.update { it.copy(pengalaman = value) }
    }

    fun setTripType(value: String) {
        _uiState.update { it.copy(tipeTrip = value) }
    }

    fun setMedia(uri: Uri?, type: MediaType) {
        _uiState.update {
            it.copy(mediaUri = uri, mediaType = type)
        }
    }

    fun submitReview() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            repository.addReview(
                kebersihan = state.kebersihan,
                pelayanan = state.pelayanan,
                lokasi = state.lokasi,
                kenyamanan = state.kenyamanan,
                pengalaman = state.pengalaman,
                tipeTrip = state.tipeTrip,
                mediaUri = state.mediaUri,
                mediaType = state.mediaType?.name
            )

            _uiState.update { it.copy(isSubmitting = false) }
        }
    }
}
