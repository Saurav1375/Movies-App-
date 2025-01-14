package com.example.moviesapp.presentation.home_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.presentation.home_screen.HomeEvents
import com.example.moviesapp.presentation.home_screen.HomeScreenViewModel
import com.example.moviesapp.presentation.home_screen.MovieState

@Composable
fun TypeChipBar(state: MovieState, viewModel: HomeScreenViewModel, modifier: Modifier = Modifier) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        TypeButton(
            modifier = Modifier
                .height(35.dp)
                .weight(1f)
                .padding(4.dp)
                .clickable {
                    viewModel.onEvents(HomeEvents.OnMediaChange(MediaType.MOVIE))

                },
            state = state,
            mediaType = MediaType.MOVIE,
            )
        TypeButton(
            modifier = Modifier
                .height(35.dp)
                .weight(1f)
                .padding(4.dp)
                .clickable {
                    viewModel.onEvents(HomeEvents.OnMediaChange(MediaType.SERIES))
                },
            state = state,
            mediaType = MediaType.SERIES
        )

    }

}

@Preview(showBackground = true)
@Composable
fun TypeChipBarPreview() {
    TypeChipBar(
        state = MovieState(),
        viewModel = hiltViewModel()
    )
}