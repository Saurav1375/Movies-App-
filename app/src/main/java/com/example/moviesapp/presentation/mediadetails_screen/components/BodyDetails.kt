package com.example.moviesapp.presentation.mediadetails_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.SeriesDetails
import com.example.moviesapp.presentation.getLanguageName
import com.example.moviesapp.presentation.getRuntimeFromMinutes
import com.example.moviesapp.presentation.getYearFromReleaseDate

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BodyDetails(media: Any, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (media) {
            is MovieDetails -> {
                MediaTitleAndTagline(
                    title = media.title,
                    tagline = media.tagline
                )
                MediaInfo(
                    releaseYear = getYearFromReleaseDate(media.releaseDate, "yyyy"),
                    language = getLanguageName(media.originalLanguage),
                    runtime = getRuntimeFromMinutes(media.runtime)
                )
                MediaStatus(status = media.status)
                RatingsBar(voteAverage = media.voteAverage.toFloat(), voteCount = media.voteCount)
                GenresSection(genres = media.genres)
                MediaOverview(overview = media.overview)
            }

            is SeriesDetails -> {
                MediaTitleAndTagline(
                    title = media.title,
                    tagline = media.tagline
                )
                MediaInfo(
                    releaseYear = getYearFromReleaseDate(media.firstAirDate, "yyyy"),
                    language = getLanguageName(media.originalLanguage),
                    runtime = null // Series don't have runtime
                )
                MediaStatus(status = media.status)
                RatingsBar(voteAverage = media.voteAverage.toFloat(), voteCount = media.voteCount)
                GenresSection(genres = media.genres)
                MediaOverview(overview = media.overview)
            }
        }
    }
}

@Composable
fun MediaTitleAndTagline(title: String, tagline: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    if (tagline.isNotEmpty()) {
        Text(
            text = tagline,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MediaInfo(releaseYear: String, language: String, runtime: String?) {
    val runtimeText = runtime?.let { " • $it" } ?: ""
    Text(
        text = "$releaseYear • $language$runtimeText",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

@Composable
fun MediaStatus(status: String) {
    if (status.isNotEmpty()) {
        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun RatingsBar(voteAverage: Float, voteCount: Int) {
    if (voteAverage >= 0 && voteCount > 0) {
        StarRatingDisplay(
            voteAverage = voteAverage,
            voteCount = voteCount
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenresSection(genres: List<String>) {
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        genres.forEach { tag ->
            TagsView(tag = tag, modifier = Modifier.sizeIn(80.dp, 25.dp))
        }
    }
}

@Composable
fun MediaOverview(overview: String) {
    if (overview.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 6.dp, top = 8.dp),
                text = "Overview",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            ExpandableText(
                overview,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            )
        }
    }
}

