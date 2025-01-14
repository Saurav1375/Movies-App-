package com.example.moviesapp.presentation.watchguide_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.MovieGenre
import com.example.moviesapp.domain.model.SearchMovie
import com.example.moviesapp.domain.model.SeriesGenre
import com.example.moviesapp.presentation.home_screen.components.MoviesSection
import com.example.moviesapp.presentation.home_screen.components.SeriesSection
import com.example.moviesapp.presentation.watchguide_screen.components.MovieGrid
import com.example.moviesapp.presentation.watchguide_screen.components.SearchBar
import com.example.moviesapp.presentation.watchguide_screen.components.SeriesGrid
import com.example.moviesapp.presentation.watchguide_screen.components.WatchChipBar
import kotlinx.coroutines.flow.retry

@SuppressLint("NewApi")
@Composable
fun WatchGuideScreen(
    modifier: Modifier = Modifier,
    viewModel: WatchGuideViewmodel = hiltViewModel(),
    onItemClick: (Int, String) -> Unit
) {
    val state by viewModel.watchGuideState.collectAsState()
    val scrollState = rememberScrollState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val moviesPagingItems = viewModel.searchMovieResults.collectAsLazyPagingItems()
    val seriesPagingItems = viewModel.searchSeriesResults.collectAsLazyPagingItems()

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            SearchBar(
                searchQuery = searchQuery,
                onQueryChange = { query ->
                    viewModel.onEvents(WatchGuideEvents.OnSearchQueryChange(query))
                },
                modifier = Modifier
            )

            if (searchQuery.isNotEmpty()) {
                if (moviesPagingItems.itemCount > 0 && state.mediaType == MediaType.MOVIE) {
                    MovieGrid(
                        pagingItems = moviesPagingItems,
                        modifier = Modifier.fillMaxSize(),
                        onClick = onItemClick,
                        onRetry = viewModel.searchMovieResults::retry
                    )
                }

                if (seriesPagingItems.itemCount > 0 && state.mediaType == MediaType.SERIES) {
                    SeriesGrid(
                        pagingItems = seriesPagingItems,
                        modifier = Modifier.fillMaxSize(),
                        onClick = onItemClick,
                        onRetry = viewModel.searchSeriesResults::retry
                    )
                }


            } else {
                Column(
                    modifier = Modifier.verticalScroll(scrollState)

                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        WatchChipBar(
                            state = state,
                            viewModel = viewModel,
                            modifier = Modifier
                                .weight(8f)
                                .padding(8.dp)
                        )
                    }


                    if (state.mediaType == MediaType.MOVIE) {
                        val genres = MovieGenre.entries.toTypedArray()

                        genres.forEach { genre ->
                            MoviesSection(
                                genre.displayName,
                                state.movieGenre.filter { it.type == genre.name }.distinct(),
                                isLoading = state.isLoading,
                                onClick = onItemClick
                            )
                        }

                    } else {
                        val genres = SeriesGenre.entries.toTypedArray()

                        genres.forEach { genre ->
                            // For series
                            SeriesSection(
                                genre.displayName,
                                state.seriesGenre.filter { it.type == genre.name }.distinct(),
                                isLoading = state.isLoading,
                                onClick = onItemClick
                            )
                        }
                    }

                }
            }


        }
    }

}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorItem(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading movies",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Retry")
        }
    }
}

@Composable
fun MovieItem(movie: SearchMovie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Movie poster
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w200${movie.posterPath}",
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}