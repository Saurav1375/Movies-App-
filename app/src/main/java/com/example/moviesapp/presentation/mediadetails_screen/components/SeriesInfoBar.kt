package com.example.moviesapp.presentation.mediadetails_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.SeriesDetails

@Composable
fun SeriesInfoBar(
    modifier: Modifier = Modifier,
    seriesDetails: SeriesDetails
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Seasons Info
            if (seriesDetails.numberOfSeasons > 0) {
                DetailItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Seasons",
                    count = seriesDetails.numberOfSeasons
                )
            }
            // Episodes Info
            if (seriesDetails.numberOfEpisodes > 0) {
                DetailItem(
                    icon = Icons.Default.PlayArrow,
                    label = "Episodes",
                    count = seriesDetails.numberOfEpisodes
                )
            }
        }
    }
}

@Composable
fun DetailItem(
    icon: ImageVector,
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    labelColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
    columnWidth: Int = 100
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .width(columnWidth.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "$label Icon",
            modifier = Modifier.size(32.dp),
            tint = iconTint
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
            color = textColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp),
            color = labelColor
        )
    }
}
