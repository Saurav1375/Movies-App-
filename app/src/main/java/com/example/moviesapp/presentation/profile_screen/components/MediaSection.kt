package com.example.moviesapp.presentation.profile_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.presentation.home_screen.components.ShimmerEffect

@Composable
fun MediaSection(
    items: List<Media>,
    modifier: Modifier = Modifier,
    isLoading : Boolean = false,
    onClick: (Int, String) -> Unit
) {

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        reverseLayout = true
    ) {
        if (isLoading) {
            items(5) {
                ShimmerEffect(modifier = Modifier.size(120.dp, 205.dp))
            }
        }
        else{
            items(items.distinct(), key = {it.toString()}) { media ->
                MediaItemView(
                    item = media, modifier = Modifier
                        .size(120.dp, 205.dp)
                        .clickable { onClick(media.id,  media.type) }
                )

            }

        }

    }

}