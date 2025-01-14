package com.example.moviesapp.presentation.mediadetails_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.Cast

@Composable
fun CastSection(
    items: List<Cast>,
    creditsType: String = "Cast",
    modifier: Modifier = Modifier,
    cardWidth: Int = 120,
    cardHeight: Int = 160,
    horizontalSpacing: Int = 16,
    verticalSpacing: Int = 32
) {
    Column(
        modifier = modifier.padding(vertical = verticalSpacing.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing.dp)
    ) {
        // Section Title
        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = creditsType,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (items.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(items, key = { it.toString() }) { person ->
                    CastItemView(
                        item = person,
                        modifier = Modifier.size(cardWidth.dp, cardHeight.dp)
                    )
                }
            }
        } else {
            // Fallback for Empty List
            Text(
                text = "No $creditsType available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}
