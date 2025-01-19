package com.example.moviesapp.presentation.friends_profile_screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.moviesapp.presentation.Screen
import com.example.moviesapp.presentation.profile_screen.EmptyStateMessage
import com.example.moviesapp.presentation.profile_screen.components.TopBar
import com.example.moviesapp.presentation.shared.UserListViewModel
import com.example.moviesapp.presentation.social_screen.UserCard

@Composable
fun FriendsFriendsInfoScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userListViewModel: UserListViewModel = hiltViewModel(),
    viewModel: FriendsProfileViewModel = hiltViewModel(),
) {
    val friendsData by viewModel.friendData.collectAsState()
    val state by userListViewModel.userData.collectAsState()
    val friendState by viewModel.friendsData.collectAsState()
    println("FriendsData: $friendsData")

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 100.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(friendsData) { friend ->
                    UserCard(
                        user = friend,
                        currentUserData = state.userData,
                        onClick =  { friendsId ->
                            navController.navigate(Screen.FriendsProfileScreen.route + "/$friendsId")
                        }

                    ) {
                        viewModel.onEvents(FriendsProfileEvents.OnAddFriend(friend.userId, state.userData.userId))
                    }
                }

                if (friendsData.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillParentMaxHeight()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            EmptyStateMessage(
                                message = "No Friends Yet\nStart connecting with others!",
                                icon = Icons.Outlined.PersonAddAlt1
                            )

                        }
                    }
                }
            }
        }

        TopBar(
            title = "${friendState.userData.username}'s Friends",
            onBackClick = { navController.navigateUp() },
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        )
    }
}