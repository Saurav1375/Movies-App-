package com.example.moviesapp.presentation.home_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.presentation.home_screen.components.FilterButton
import com.example.moviesapp.presentation.home_screen.components.MoviesSection
import com.example.moviesapp.presentation.home_screen.components.SeriesSection
import com.example.moviesapp.presentation.home_screen.components.TypeChipBar
import com.example.moviesapp.presentation.home_screen.components.UserInfoBar
import com.example.moviesapp.presentation.shared.SharedUserViewModel

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    sharedUserViewModel: SharedUserViewModel,
    onItemClick: (Int, String) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        val user by sharedUserViewModel.user.collectAsState()
        val state by homeScreenViewModel.state.collectAsState()
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            UserInfoBar(
                user = user, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TypeChipBar(
                    state = state,
                    viewModel = homeScreenViewModel,
                    modifier = Modifier
                        .weight(8f)
                        .padding(8.dp)
                )

                FilterButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(2f),
                    onClick = {
                        homeScreenViewModel.onEvents(HomeEvents.OnLanguageChange("hi-IN"))
                    }
                )

            }



            if (state.mediaType == MediaType.MOVIE) {
                MoviesSection("In Theatres", state.movieType.theatreMovie,isLoading = state.isLoading, onClick = onItemClick)
                MoviesSection("Popular", state.movieType.popularMovies,isLoading = state.isLoading, onClick = onItemClick)
                MoviesSection("Upcoming", state.movieType.upcomingMovies,isLoading = state.isLoading, onClick = onItemClick)
                MoviesSection("Top Rated", state.movieType.topRatedMovies,isLoading = state.isLoading, onClick = onItemClick)

            } else {
                SeriesSection("Airing Today", state.seriesType.airingTodaySeries,isLoading = state.isLoading, onClick = onItemClick)
                SeriesSection("On The Air", state.seriesType.onTheAirSeries,isLoading = state.isLoading, onClick = onItemClick)
                SeriesSection("Popular", state.seriesType.popularSeries,isLoading = state.isLoading, onClick = onItemClick)
                SeriesSection("Top Rated", state.seriesType.topRatedSeries,isLoading = state.isLoading, onClick = onItemClick)

            }


        }

    }
}