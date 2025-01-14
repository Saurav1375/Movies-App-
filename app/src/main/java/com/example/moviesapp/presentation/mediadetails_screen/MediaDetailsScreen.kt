package com.example.moviesapp.presentation.mediadetails_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.presentation.home_screen.components.MoviesSection
import com.example.moviesapp.presentation.mediadetails_screen.components.BackdropImageView
import com.example.moviesapp.presentation.mediadetails_screen.components.CastSection
import com.example.moviesapp.presentation.mediadetails_screen.components.HeaderDetails
import com.example.moviesapp.presentation.mediadetails_screen.components.MovieDetailsScreen
import com.example.moviesapp.presentation.mediadetails_screen.components.SeriesDetailsScreen
import com.example.moviesapp.presentation.mediadetails_screen.components.TrailerView
import com.example.moviesapp.presentation.shared.UserListViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MediaDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    detailsViewModel: MediaDetailsViewModel,
    userListViewModel: UserListViewModel = hiltViewModel(),
    mediaType: MediaType,
    mediaId: Int,
    onItemClick: (Int, String) -> Unit
) {
    val mediaDetailsState by detailsViewModel.mediaDetailsState.collectAsState()
    val userDetailState by userListViewModel.userData.collectAsState()


    when (mediaType) {
        MediaType.MOVIE -> MovieDetailsScreen(
            modifier,
            detailsViewModel,
            mediaDetailsState,
            userDetailState,
            mediaId,
            onItemClick
        ) {
            navController.navigateUp()
        }

        MediaType.SERIES -> SeriesDetailsScreen(
            modifier,
            mediaDetailsState,
            detailsViewModel,
            userDetailState,
            mediaId,
            onItemClick
        ) {
            navController.navigateUp()

        }

    }


}
