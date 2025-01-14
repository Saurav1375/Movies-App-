package com.example.moviesapp.presentation.mediadetails_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesapp.R
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.SeriesDetails
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsState
import com.example.moviesapp.utils.Constants

@Composable
fun BackdropImageView(media: Any, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        val imageUrl = when (media) {
            is MovieDetails -> {
                if (media.backdropPath.isNotEmpty()) {
                    "https://image.tmdb.org/t/p/original/${media.backdropPath}"
                } else {
                    "https://image.tmdb.org/t/p/original/${media.posterPath}"
                }
            }
            is SeriesDetails -> {
                if (media.backdropPath.isNotEmpty()) {
                    "https://image.tmdb.org/t/p/original/${media.backdropPath}"
                } else {
                    "https://image.tmdb.org/t/p/original/${media.posterPath}"
                }
            }
            else -> {
                // Handle default case or show a placeholder image if the media type is not recognized
                "https://via.placeholder.com/500"
            }
        }

        val contentDescription = when (media) {
            is MovieDetails -> "Backdrop image for the movie: ${media.title}"
            is SeriesDetails -> "Backdrop image for the series: ${media.title}"
            else -> "Backdrop image"
        }

        AsyncImage(
            model =  ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .error(R.drawable.errorholder)
                .placeholder(android.R.drawable.menu_frame)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
