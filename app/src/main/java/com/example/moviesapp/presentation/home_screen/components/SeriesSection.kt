package com.example.moviesapp.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SeriesSection(type: String, items: List<Series>, modifier: Modifier = Modifier,isLoading: Boolean = false, onClick: (Int, String) -> Unit) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface

            )
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "See all",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary

            )

        }


        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                items(5) {
                    ShimmerEffect(modifier = Modifier.size(120.dp, 205.dp))
                }
            }
            else {
                items(items, key = { it.toString() }) { series ->
                    SeriesViewItem(
                        item = series, modifier = Modifier
                            .size(120.dp, 205.dp).clickable {  onClick(series.id, "SERIES")}
                    )

                }
            }

        }

    }


}