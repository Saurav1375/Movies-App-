package com.example.moviesapp.presentation.friends_profile_screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.presentation.Screen
import com.example.moviesapp.presentation.profile_screen.EmptyStateMessage
import com.example.moviesapp.presentation.profile_screen.components.MediaSection
import com.example.moviesapp.presentation.profile_screen.components.ProfileHeader
import com.example.moviesapp.presentation.profile_screen.components.SectionTitle
import com.example.moviesapp.presentation.profile_screen.components.WatchlistCard
import com.example.moviesapp.presentation.shared.UserListViewModel

@Composable
fun FriendsProfileScreen(
    navController: NavHostController,
    navigateToFriends: () -> Unit = {},
    userListViewModel: FriendsProfileViewModel = hiltViewModel(),
    friendsId: String,
    modifier: Modifier = Modifier,
    navigateToWatchlist: (String) -> Unit,
) {
    val mediaListState by userListViewModel.mediaList.collectAsState()
    val friendState by userListViewModel.friendsData.collectAsState()
    val friendsData by userListViewModel.friendData.collectAsState()
    val friendProfile = friendState.userData

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
                    profilePic = friendProfile.profilePictureUrl,
                    username = friendProfile.username,
                    email = friendProfile.email,
                    handle = "@moviebuff",
                    friendsCount = friendsData.size,
                    onFriendsClick = navigateToFriends
                )
            }

            // Watchlists Section
            item {
                SectionTitle(title = "Watchlists")
            }

            item {
                val watchlists = mediaListState.filter { it.type == ListType.WATCHLIST.name }
                if (watchlists.isEmpty()) {
                    EmptyStateMessage(
                        message = "Create your first watchlist to keep track of movies you want to watch!",
                        icon = Icons.AutoMirrored.Outlined.PlaylistAdd
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(watchlists.distinct(), key = { it.toString() }) { watchlist ->
                            WatchlistCard(
                                watchlist = watchlist,
                                onClick = navigateToWatchlist
                            )
                        }
                    }
                }
            }

            // Favorites Section
            item {
                SectionTitle(title = "Favorites")
            }

            item {
                val favorites =
                    friendProfile.mediaLists.find { it.type == ListType.FAVOURITES.name }?.list
                        ?: emptyList()
                if (favorites.isEmpty()) {
                    EmptyStateMessage(
                        message = "Add movies and shows you love to your favorites!",
                        icon = Icons.Outlined.Favorite
                    )
                } else {
                    MediaSection(
                        items = favorites,
                        isLoading = friendState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) { it, mediaType ->
                        navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")
                    }
                }
            }

            // Watched Section
            item {
                SectionTitle(title = "Watched")
            }

            item {
                val watched =
                    friendProfile.mediaLists.find { it.type == ListType.WATCHED.name }?.list
                        ?: emptyList()
                if (watched.isEmpty()) {
                    EmptyStateMessage(
                        message = "Track what you've watched by marking movies and shows as watched!",
                        icon = Icons.Outlined.CheckCircle
                    )
                } else {
                    MediaSection(
                        items = watched,
                        isLoading = friendState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) { it, mediaType ->
                        navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")
                    }
                }
            }
        }
    }
}
