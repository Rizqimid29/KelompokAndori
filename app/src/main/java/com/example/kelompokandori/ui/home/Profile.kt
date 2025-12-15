package com.example.kelompokandori.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.kelompokandori.MainActivity
import com.example.kelompokandori.SupabaseClient
import com.example.kelompokandori.ui.article.ArticleListActivity
import com.example.kelompokandori.ui.trip.TripActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    @SerialName("full_name") val fullName: String?,
    @SerialName("avatar_url") val avatarUrl: String?
)

class ProfileViewModel : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _email = MutableStateFlow<String>("")
    val email = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()

                if (currentUser != null) {
                    _email.value = currentUser.email ?: "No Email"

                    val profileData = SupabaseClient.client.from("profiles")
                        .select {
                            filter {
                                eq("id", currentUser.id)
                            }
                        }
                        .decodeSingleOrNull<UserProfile>()

                    _userProfile.value = profileData
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
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

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val email by viewModel.email.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val PrimaryColor = Color(0xFF9AA9E3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profil Pengguna",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(color = PrimaryColor)
        } else {
            if (userProfile?.avatarUrl != null) {
                AsyncImage(
                    model = userProfile!!.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- DATA DIRI ---
            ProfileInfoItem(
                icon = Icons.Default.Person,
                label = "Nama Lengkap",
                value = userProfile?.fullName ?: "Belum diisi"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoItem(
                icon = Icons.Default.Email,
                label = "Email",
                value = email
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- TOMBOL NAVIGASI LAIN ---
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, TripActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Rencana Perjalanan (Trip Planner)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, ArticleListActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Tips & Pengalaman Wisata")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.logout {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F6FA), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF9AA9E3))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}