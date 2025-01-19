package com.example.moviesapp.presentation.profile_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.presentation.Screen
import com.example.moviesapp.presentation.profile_screen.components.MediaSection
import com.example.moviesapp.presentation.profile_screen.components.ProfileHeader
import com.example.moviesapp.presentation.profile_screen.components.SectionTitle
import com.example.moviesapp.presentation.profile_screen.components.WatchlistCard
import com.example.moviesapp.presentation.shared.UserListViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    navigateToFriends: () -> Unit = {},
    userListViewModel: UserListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    navigateToWatchlist: (String) -> Unit,
) {
    var expandedSection by remember { mutableStateOf<String?>(null) }
    val userState by userListViewModel.userData.collectAsState()
    val mediaListState by userListViewModel._mediaListState.collectAsState()
    val friendsData by userListViewModel.friendData.collectAsState()
    val userData = userState.userData

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
                        items(watchlists.distinct(), key ={ it.toString() }) { watchlist ->
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
                val favorites = userData.mediaLists.find { it.type == ListType.FAVOURITES.name }?.list ?: emptyList()
                if (favorites.isEmpty()) {
                    EmptyStateMessage(
                        message = "Add movies and shows you love to your favorites!",
                        icon = Icons.Outlined.Favorite
                    )
                } else {
                    MediaSection(
                        items = favorites,
                        isLoading = userState.isLoading,
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
                val watched = userData.mediaLists.find { it.type == ListType.WATCHED.name }?.list ?: emptyList()
                if (watched.isEmpty()) {
                    EmptyStateMessage(
                        message = "Track what you've watched by marking movies and shows as watched!",
                        icon = Icons.Outlined.CheckCircle
                    )
                } else {
                    MediaSection(
                        items = watched,
                        isLoading = userState.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) { it, mediaType ->
                        navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}