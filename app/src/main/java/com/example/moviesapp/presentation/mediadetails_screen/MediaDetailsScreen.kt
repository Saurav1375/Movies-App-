package com.example.moviesapp.presentation.mediadetails_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.presentation.mediadetails_screen.components.MovieDetailsScreen
import com.example.moviesapp.presentation.mediadetails_screen.components.SeriesDetailsScreen
import com.example.moviesapp.presentation.shared.UserListViewModel

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
    val mediaDetailsState by detailsViewModel.mediaDetailsUiState.collectAsState()
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
