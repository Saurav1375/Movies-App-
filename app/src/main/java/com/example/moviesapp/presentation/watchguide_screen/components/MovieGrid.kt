package com.example.moviesapp.presentation.watchguide_screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesapp.R
import com.example.moviesapp.domain.model.SearchMovie
import com.example.moviesapp.presentation.getYearFromReleaseDate
import com.example.moviesapp.utils.Constants

@Composable
fun MovieGrid(
    pagingItems: LazyPagingItems<SearchMovie>,
    onClick: (Int, String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index -> pagingItems[index]?.id ?: index }
        ) { index ->
            pagingItems[index]?.let { movie ->
                MovieGridItem(
                    item = movie, modifier = Modifier
                        .size(120.dp, 260.dp)
                        .clickable { onClick(movie.id, "MOVIE") }
                )
            }
        }

        when (pagingItems.loadState.append) {
            is LoadState.Loading -> {
                item(
                    span = { GridItemSpan(2) }
                ) {
                    LoadingItem()
                }
            }

            is LoadState.Error -> {
                item(
                    span = { GridItemSpan(2) }
                ) {
                    ErrorItem(
                        onRetry = { pagingItems.retry() }
                    )
                }
            }

            else -> {}
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun MovieGridItem(
    item: SearchMovie,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.FillBounds,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Constants.IMAGE_BASE_URL + item.posterPath)
                        .crossfade(enable = true)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(R.drawable.errorholder)
                        .build(),
                    contentDescription = "Movie Poster"
                )
            }

            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = item.title,
                maxLines = 1,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = getYearFromReleaseDate(item.releaseDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
fun LoadingItem(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun ErrorItem(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Error loading movies",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Try Again")
        }
    }
}
