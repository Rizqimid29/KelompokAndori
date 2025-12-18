package com.example.kelompokandori.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kelompokandori.model.Review

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Row {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${review.overall_rating}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                text = review.description,
                style = MaterialTheme.typography.bodyMedium
            )

            review.trip_type?.let {
                Text(
                    text = "Tipe Trip: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
