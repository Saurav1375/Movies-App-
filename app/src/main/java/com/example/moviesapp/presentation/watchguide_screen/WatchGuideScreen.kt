package com.example.moviesapp.presentation.watchguide_screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.MovieGenre
import com.example.moviesapp.domain.model.SearchMovie
import com.example.moviesapp.domain.model.SeriesGenre
import com.example.moviesapp.presentation.Screen
import com.example.moviesapp.presentation.home_screen.HomeEvents
import com.example.moviesapp.presentation.home_screen.components.MoviesSection
import com.example.moviesapp.presentation.home_screen.components.SeriesSection
import com.example.moviesapp.presentation.watchguide_screen.components.MovieGrid
import com.example.moviesapp.presentation.watchguide_screen.components.SearchBar
import com.example.moviesapp.presentation.watchguide_screen.components.SeriesGrid
import com.example.moviesapp.presentation.watchguide_screen.components.WatchChipBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.retry

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun WatchGuideScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: WatchGuideViewmodel = hiltViewModel(),
    onItemClick: (Int, String) -> Unit
) {

    val state by viewModel.watchGuideState.collectAsState()
    val scrollState = rememberScrollState()
    val swipeState = rememberSwipeRefreshState(
        isRefreshing = state.isRefreshing
    )





    val searchQuery by viewModel.searchQuery.collectAsState()
    val moviesPagingItems = viewModel.searchMovieResults.collectAsLazyPagingItems()
    val seriesPagingItems = viewModel.searchSeriesResults.collectAsLazyPagingItems()

    BackHandler {
        if(searchQuery.isNotEmpty()){
            viewModel.onEvents(WatchGuideEvents.OnSearchQueryChange(""))
        }
        else{
            navController.navigate(Screen.HomeScreen.route)
        }
    }

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
                SwipeRefresh(
                    state = swipeState,
                    onRefresh = {viewModel.onEvents(WatchGuideEvents.Refresh)}
                ) {
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
                                if (state.movieGenre.filter { it.type == genre.name }.distinct().isNotEmpty()){
                                    MoviesSection(
                                        genre.displayName,
                                        state.movieGenre.filter { it.type == genre.name }.distinct(),
                                        isLoading = state.isLoading,
                                        onClick = onItemClick
                                    )
                                }

                            }

                        } else {
                            val genres = SeriesGenre.entries.toTypedArray()


                            genres.forEach { genre ->
                                // For series
                                if(state.seriesGenre.filter { it.type == genre.name }.distinct().isNotEmpty()) {
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

    }

}
