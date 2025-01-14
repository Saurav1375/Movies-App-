package com.example.moviesapp.presentation.mediadetails_screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun StarRatingDisplay(
    voteAverage: Float,
    voteCount: Int,
    maxStars: Int = 5,
    starColor: Color = MaterialTheme.colorScheme.secondary,
    modifier: Modifier = Modifier
) {
    val filledStars = (voteAverage / 2).toInt()
    val hasHalfStar = (voteAverage / 2) % 1 >= 0.3
    val emptyStars = maxStars - filledStars - if (hasHalfStar) 1 else 0
    val ratingForStars = String.format("%.1f", voteAverage / 2).toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row for stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(filledStars) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Filled Star",
                    tint = starColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (hasHalfStar) {
                Icon(
                    imageVector = Icons.Filled.StarHalf,
                    contentDescription = "Half Star",
                    tint = starColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            repeat(emptyStars) {
                Icon(
                    imageVector = Icons.Filled.StarBorder,
                    contentDescription = "Empty Star",
                    tint = starColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Rating Text
        Text(
            text = "$ratingForStars/5 ($voteCount votes)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
