package com.example.kelompokandori.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ReviewSummaryScreen(
    rating: Float,
    comment: String
) {
    val fullStars = rating.toInt()
    val hasHalfStar = rating - fullStars >= 0.5f
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Kesimpulan Review",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

     
        Row {
            repeat(fullStars) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(32.dp)
                )
            }

            if (hasHalfStar) {
                Icon(
                    imageVector = Icons.Filled.StarHalf,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(32.dp)
                )
            }

            repeat(emptyStars) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = String.format("%.1f / 5.0", rating),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = comment,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
