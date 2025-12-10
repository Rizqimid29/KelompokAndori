package com.example.kelompokandori.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelompokandori.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthViewModel : ViewModel() {

    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    var fullName = MutableStateFlow("")

    private val _isLoginMode = MutableStateFlow(true)
    val isLoginMode = _isLoginMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage = _uiMessage.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    fun toggleMode() {
        _isLoginMode.value = !_isLoginMode.value
        _uiMessage.value = null
    }

    fun submit() {
        val currentEmail = email.value
        val currentPass = password.value
        val currentName = fullName.value

        if (currentEmail.isBlank() || currentPass.isBlank()) {
            _uiMessage.value = "Email dan Password wajib diisi"
            return
        }

        if (!_isLoginMode.value && currentName.isBlank()) {
            _uiMessage.value = "Nama Lengkap wajib diisi"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _uiMessage.value = null
            try {
                val auth = SupabaseClient.client.auth

                if (_isLoginMode.value) {
                    auth.signInWith(Email) {
                        email = currentEmail
                        password = currentPass
                    }
                    _uiMessage.value = "Login Berhasil!"
                    _loginSuccess.value = true
                } else {
                    auth.signUpWith(Email) {
                        email = currentEmail
                        password = currentPass
                        data = buildJsonObject {
                            put("full_name", currentName)
                            put("avatar_url", "https://ui-avatars.com/api/?name=$currentName&background=random")
                        }
                    }
                    _uiMessage.value = "Register Berhasil! Silakan Login."
                    _isLoginMode.value = true
                }
            } catch (e: Exception) {
                _uiMessage.value = "Gagal: ${e.message?.take(50)}..."
            } finally {
                _isLoading.value = false
            }
        }
    }
}