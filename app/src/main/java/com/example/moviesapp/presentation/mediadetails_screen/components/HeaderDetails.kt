package com.example.moviesapp.presentation.mediadetails_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.SeriesDetails


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderDetails(media: Any, modifier: Modifier = Modifier) {

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            when (media) {
                is MovieDetails -> BodyDetails(
                    media, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                is SeriesDetails -> BodyDetails(
                    media, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

            }


        }
    }

}