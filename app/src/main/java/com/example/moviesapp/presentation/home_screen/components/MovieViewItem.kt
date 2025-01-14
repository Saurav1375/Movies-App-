package com.example.moviesapp.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviesapp.R
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.presentation.getYearFromReleaseDate
import com.example.moviesapp.utils.Constants
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MovieViewItem(item: Movie, modifier: Modifier = Modifier) {
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
                        .crossfade(true)
                        .error(R.drawable.errorholder)
                        .placeholder(android.R.drawable.menu_frame)
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


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovieViewItemPreview() {
    MovieViewItem(
        modifier = Modifier
            .size(100.dp, 160.dp)
            .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary)),
        item = Movie(
            id = 1,
            title = "Spider Man",
            posterPath = "",
            releaseDate = LocalDate.now().toString(),
            type = ""
        )
    )
}