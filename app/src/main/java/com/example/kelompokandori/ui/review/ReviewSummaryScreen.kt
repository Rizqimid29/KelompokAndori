package com.example.kelompokandori.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun ReviewSummaryScreen(
    rating: Float,
    comment: String,
    mediaUrl: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Terima kasih atas review Anda 🙏",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Masukan Anda sangat membantu pengguna lain.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // ⭐ BINTANG SESUAI RATING AKHIR
        Row(verticalAlignment = Alignment.CenterVertically) {
            StarRating(rating = rating)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = rating.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFEDEDED),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = comment)
        }

        if (!mediaUrl.isNullOrEmpty()) {
            AsyncImage(
                model = mediaUrl,
                contentDescription = "Review Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}
