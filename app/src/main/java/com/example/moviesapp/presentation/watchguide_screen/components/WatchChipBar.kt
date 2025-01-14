package com.example.moviesapp.presentation.watchguide_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.presentation.watchguide_screen.WatchGuideEvents
import com.example.moviesapp.presentation.watchguide_screen.WatchGuideState
import com.example.moviesapp.presentation.watchguide_screen.WatchGuideViewmodel

@Composable
fun WatchChipBar(
    state: WatchGuideState,
    viewModel: WatchGuideViewmodel,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        WatchGuideButton(
            modifier = Modifier
                .height(35.dp)
                .weight(1f)
                .padding(4.dp)
                .clickable {
                    viewModel.onEvents(WatchGuideEvents.OnMediaChange(MediaType.MOVIE))

                },
            state = state,
            mediaType = MediaType.MOVIE,
        )
        WatchGuideButton(
            modifier = Modifier
                .height(35.dp)
                .weight(1f)
                .padding(4.dp)
                .clickable {
                    viewModel.onEvents(WatchGuideEvents.OnMediaChange(MediaType.SERIES))
                },
            state = state,
            mediaType = MediaType.SERIES
        )

    }

}