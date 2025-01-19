package com.example.moviesapp.presentation.mediadetails_screen.components


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moviesapp.presentation.home_screen.components.MoviesSection
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsEvent
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsUiState
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel
import com.example.moviesapp.presentation.mediadetails_screen.components.chatComponents.MovieChatSection
import com.example.moviesapp.presentation.profile_screen.UserDataState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MovieDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MediaDetailsViewModel,
    mediaDetailsUiState: MediaDetailsUiState,
    userDetailState: UserDataState,
    mediaId: Int,
    onItemClick: (Int, String) -> Unit,
    onBackClick: () -> Unit // Back click handler
) {

    var showAddDialog by remember { mutableStateOf(false) }
    var youTubePlayerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = mediaDetailsUiState.isRefreshing
    )

    val messages by viewModel.mediaRoomMessage.collectAsStateWithLifecycle()
    val user = userDetailState.userData.userId

    val mediaList = userDetailState.userData.mediaLists
    println("list : $mediaList")

    DisposableEffect(Unit) {
        onDispose {
            youTubePlayerView?.release() // Release the player when the Composable is removed
        }
    }

    if (showAddDialog) {
        AddItemsDialog(
            mediaLists = mediaList,
            viewModel = viewModel,
            mediaId = mediaId,
            userId = user,
            onDismiss = { showAddDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.OnEvent(MediaDetailsEvent.OnRefresh) }
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
                    .align(Alignment.BottomEnd)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                mediaDetailsUiState.movieDetails?.let {
                    BackdropImageView(
                        it,
                        Modifier
                            .height(LocalConfiguration.current.screenHeightDp.dp / 1.7f)
                            .fillMaxWidth()
                    )
                }

                // Header
                mediaDetailsUiState.movieDetails?.let {
                    HeaderDetails(
                        it,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Cast section
                mediaDetailsUiState.credits?.cast?.let {
                    CastSection(
                        it,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Trailer section
                mediaDetailsUiState.videoUrl?.let { id ->
                    TrailerView(
                        videoId = id,
                        onYouTubePlayerCreated = { youTubePlayerView = it }
                    )
                }

                // Recommendations section
                if (mediaDetailsUiState.movieRecommendations.isNotEmpty()) {
                    MoviesSection(
                        "Recommended",
                        mediaDetailsUiState.movieRecommendations,
                        onClick = onItemClick,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                MovieChatSection(
                    messages = messages ,
                    viewModel = viewModel,
                    currentUser = userDetailState.userData,
                )

            }
        }

        // Back navigation and Add button
        Column {
            TopAppBar(
                title = { Text("Movie Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to list",
                            tint = MaterialTheme.colorScheme.onBackground

                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF1F1F1F)
                )
            )
        }
    }
}

@SuppressLint("NewApi")
@Preview
@Composable
fun PreviewMovieDetailsScreen() {
    MovieDetailsScreen(
        viewModel = hiltViewModel(),
        mediaDetailsUiState = MediaDetailsUiState(),
        userDetailState = UserDataState(),
        mediaId = 123,
        onItemClick = { _, _ -> },
        onBackClick = { /* Handle back action */ }
    )
}
