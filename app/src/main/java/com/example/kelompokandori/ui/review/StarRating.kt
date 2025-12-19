package com.example.kelompokandori.ui.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StarRating(
    rating: Float,
    modifier: Modifier = Modifier,
    starSize: Int = 28
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector =
                    if (i <= rating.toInt())
                        Icons.Filled.Star
                    else
                        Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating.toInt())
                    Color(0xFFFFC107)
                else
                    Color.Gray,
                modifier = Modifier.size(starSize.dp)
            )
        }
    }
}
