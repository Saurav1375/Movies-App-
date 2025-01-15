package com.example.moviesapp.presentation.mediadetails_screen.components


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.moviesapp.presentation.home_screen.components.SeriesSection
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsUiState
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel
import com.example.moviesapp.presentation.profile_screen.UserDataState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SeriesDetailsScreen(
    modifier: Modifier = Modifier,
    mediaDetailsUiState: MediaDetailsUiState,
    viewModel: MediaDetailsViewModel,
    userDetailState: UserDataState,
    mediaId : Int,
    onItemClick: (Int, String) -> Unit,
    onBackClick: () -> Unit, // Add a lambda for back navigation
) {

    var youTubePlayerView by remember { mutableStateOf<YouTubePlayerView?>(null) }
    val mediaList = userDetailState.userData.mediaLists

    DisposableEffect(Unit) {
        onDispose {
            youTubePlayerView?.release() // Clean up the player when the screen is disposed
        }
    }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddItemsDialog(
            mediaLists = mediaList,
            viewModel = viewModel,
            mediaId = mediaId,
            userId = userDetailState.userData.userId,
            onDismiss = { showAddDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp) // Add space at the bottom to prevent UI overlap
                .verticalScroll(rememberScrollState()), // Allow vertical scrolling
            verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between elements
        ) {
            // Display the backdrop image if available
            mediaDetailsUiState.seriesDetails?.let {
                BackdropImageView(
                    it,
                    Modifier
                        .height(LocalConfiguration.current.screenHeightDp.dp / 1.7f) // Height of the backdrop
                        .fillMaxWidth()
                )
            }

            // Display the header details of the series (e.g., title, release date)
            mediaDetailsUiState.seriesDetails?.let {
                HeaderDetails(
                    it,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Display the cast section if available
            mediaDetailsUiState.credits?.cast?.let {
                CastSection(
                    it,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // Display the series information bar
            mediaDetailsUiState.seriesDetails?.let { SeriesInfoBar(modifier, seriesDetails = it) }

            // Display the trailer video if available
            mediaDetailsUiState.videoUrl?.let { id ->
                TrailerView(
                    videoId = id,
                    onYouTubePlayerCreated = { youTubePlayerView = it }
                )
            }

            // Display recommendations if available
            if (mediaDetailsUiState.seriesRecommendations.isNotEmpty()) {
                SeriesSection(
                    "Recommended",
                    mediaDetailsUiState.seriesRecommendations,
                    onClick = onItemClick,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Column {
            TopAppBar(
                title = { Text("Series Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {onBackClick()}) {
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