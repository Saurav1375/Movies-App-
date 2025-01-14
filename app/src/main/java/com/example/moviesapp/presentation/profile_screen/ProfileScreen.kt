package com.example.moviesapp.presentation.profile_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.presentation.Screen
import com.example.moviesapp.presentation.home_screen.components.MoviesSection
import com.example.moviesapp.presentation.shared.UserListViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    navigateToFriends: () -> Unit = {},
    userListViewModel: UserListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigateToWatchlist: (Int) -> Unit = {},
    navigateToFavorites: () -> Unit = {},
    navigateToWatched: () -> Unit = {}
) {
    var expandedSection by remember { mutableStateOf<String?>(null) }
    val userState by userListViewModel.userData.collectAsState()
    val mediaListState by userListViewModel._mediaListState.collectAsState()
    val userData = userState.userData
    println(userState.userData.mediaLists)

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    profilePic = userData.profilePictureUrl,
                    username = userData.username,
                    email = userData.email,
                    handle = "@moviebuff",
                    friendsCount = 250,
                    onFriendsClick = navigateToFriends
                )
            }

            // Watchlists Section
            item {
                SectionTitle(title = "Watchlists")
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(mediaListState) { watchlist ->
                        WatchlistCard(
                            watchlist = watchlist,
                            onClick = { navigateToWatchlist(0) }
                        )
                    }
                }
            }

            // Favorites Section
            item {
                SectionTitle(title = "Favorites")
            }

            item {
                MediaSection(
                    items = userData.mediaLists.find { it.type == ListType.FAVOURITES.name }?.list ?: emptyList(),
                    isLoading = userState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { it, mediaType ->
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")

                }
            }

            // Watched Section
            item {
                SectionTitle(title = "Watched")
            }

            item {
                MediaSection(
                    items = userData.mediaLists.find { it.type == ListType.WATCHED.name }?.list ?: emptyList(),
                    isLoading = userState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { it, mediaType ->
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")

                }
            }
        }
    }
}