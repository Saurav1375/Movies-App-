package com.example.moviesapp.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector = Icons.Filled.Home,
    val unselectedIcon: ImageVector = Icons.Outlined.Home
) {
    object HomeScreen : Screen(
        route = "home_screen",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    object WatchScreen : Screen(
        route = "watch",
        title = "Watch",
        selectedIcon = Icons.Filled.PlayCircle,
        unselectedIcon = Icons.Outlined.PlayCircle
    )

    object SocialScreen : Screen(
        route = "social",
        title = "Social",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    )

    object ProfileScreen : Screen(
        route = "profile_screen",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    data object MediaDetailsScreen : Screen(
        route = "media_details_screen",
        title = "Media Details",

    )

     object WatchListDetailedView : Screen(
        route = "watchlist_detailed_view",
        title = "Watchlist Detailed View",
    )

    object FriendsWatchListDetailedView : Screen(
        route = "friends_watchlist_detailed_view",
        title = "Friends Watchlist Detailed View",
    )


    object FriendsInfoScreen : Screen(
        route = "friends_info_screen",
        title = "Friends Info Screen",
    )
    object FriendsFriendsInfoScreen : Screen(
        route = "friends_friends_info_screen",
        title = "Friends Friends Info Screen",
    )
    object FriendsProfileScreen : Screen(
        route = "friends_profile_screen",
        title = "Friends Profile Screen",
    )
    data object AuthScreen : Screen("auth_screen")
    data object TestScreen : Screen("test_screen")
    data object SplashScreen : Screen("splash_screen")

}