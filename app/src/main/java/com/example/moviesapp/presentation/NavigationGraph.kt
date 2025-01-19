package com.example.moviesapp.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.presentation.auth.AuthScreen
import com.example.moviesapp.presentation.auth.AuthViewModel
import com.example.moviesapp.presentation.auth.TestScreen
import com.example.moviesapp.presentation.friends_profile_screen.FriendsFriendsInfoScreen
import com.example.moviesapp.presentation.home_screen.HomeScreen
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsScreen
import com.example.moviesapp.presentation.mediadetails_screen.MediaDetailsViewModel
import com.example.moviesapp.presentation.profile_screen.FriendsInfoScreen
import com.example.moviesapp.presentation.friends_profile_screen.FriendsProfileScreen
import com.example.moviesapp.presentation.friends_profile_screen.FriendsProfileViewModel
import com.example.moviesapp.presentation.friends_profile_screen.FriendsWatchListDetailedView
import com.example.moviesapp.presentation.profile_screen.ProfileScreen
import com.example.moviesapp.presentation.profile_screen.WatchListDetailedView
import com.example.moviesapp.presentation.shared.SharedUserViewModel
import com.example.moviesapp.presentation.social_screen.SocialScreen
import com.example.moviesapp.presentation.watchguide_screen.WatchGuideScreen

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(modifier: Modifier = Modifier, navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val sharedUserViewModel: SharedUserViewModel = hiltViewModel()
    val user by sharedUserViewModel.user.collectAsState()

    LaunchedEffect(user != null) {
        if (user != null) {
            navController.navigate(Screen.HomeScreen.route)
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            val showBottomBar = remember(currentRoute) {
                currentRoute in listOf(
                    Screen.HomeScreen.route,
                    Screen.WatchScreen.route,
                    Screen.SocialScreen.route,
                    Screen.ProfileScreen.route
                )
            }


            MovieAppNavigation(navController = navController)

        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.AuthScreen.route) {
            composable(Screen.AuthScreen.route) {
                AuthScreen(viewModel = authViewModel, onAuthSuccess = { user ->
                    navController.navigate(Screen.HomeScreen.route)
                })
            }
            composable(Screen.TestScreen.route) {
                TestScreen(
                    modifier = modifier,
                    sharedUserViewModel = sharedUserViewModel,
                    authViewModel = authViewModel
                ) {
                    navController.navigate(Screen.AuthScreen.route)
                }
            }


            composable(
                route = Screen.HomeScreen.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { 1000 })
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -1000 })
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 })
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 })
                }
            ) { _ ->
                HomeScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp),
                    sharedUserViewModel = sharedUserViewModel
                ) { it, mediaType ->
                    println("movieId: $it")
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")

                }
            }


            composable(
                route = Screen.MediaDetailsScreen.route + "/{mediaId}/{mediaType}",
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -1000 }) + fadeOut()
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -1000 }) + fadeIn()
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut()
                }
            ) { backStackEntry ->
                val detailsViewModel: MediaDetailsViewModel = hiltViewModel(backStackEntry)
                MediaDetailsScreen(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp),
                    navController = navController,
                    detailsViewModel = detailsViewModel,
                    mediaId = backStackEntry.arguments?.getString("mediaId")!!.toInt(),
                    mediaType = MediaType.valueOf(backStackEntry.arguments?.getString("mediaType")!!)
                ) { it, mediaType ->
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")

                }
            }


            composable(
                route = Screen.ProfileScreen.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) }
            ) {
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    navigateToFriends = {
                        navController.navigate(Screen.FriendsInfoScreen.route)
                    }
                ) { listId ->
                    navController.navigate(Screen.WatchListDetailedView.route + "/$listId")
                }
            }

            composable(
                route = Screen.WatchScreen.route,
            ) { _ ->
                WatchGuideScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp),
                    navController
                ) { it, mediaType ->
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")
                }
            }

            composable(
                route = Screen.SocialScreen.route,
            ) {
                SocialScreen(
                    navController = navController
                ) {

                }
            }

// Corrected WatchListDetailedView composable
            composable(
                route = Screen.WatchListDetailedView.route + "/{listId}",
                arguments = listOf(navArgument("listId") { type = NavType.StringType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getString("listId") ?: return@composable

                WatchListDetailedView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp),
                    listId = listId,
                    onBackPress = {
                        navController.popBackStack()
                    }
                ) { it, mediaType ->
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")
                }
            }

            composable(
                route = Screen.FriendsWatchListDetailedView.route + "/{friendListId}",
                arguments = listOf(navArgument("friendListId") { type = NavType.StringType })
            ) { backStackEntry ->
                val listId = backStackEntry.arguments?.getString("friendListId") ?: return@composable
                val viewModel : FriendsProfileViewModel = hiltViewModel()
                FriendsWatchListDetailedView (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp),
                    listId = listId,
                    viewModel = viewModel,
                    onBackPress = {
                        navController.popBackStack()
                    }
                ) { it, mediaType ->
                    navController.navigate(Screen.MediaDetailsScreen.route + "/$it/$mediaType")
                }
            }

            composable(Screen.FriendsInfoScreen.route) {
                FriendsInfoScreen(
                    navController = navController,
                )
            }
            composable(Screen.FriendsFriendsInfoScreen.route) {
                FriendsFriendsInfoScreen(
                    navController = navController,
                )
            }

            composable(Screen.FriendsProfileScreen.route + "/{friendsId}") { backStackEntry ->
                val viewModel : FriendsProfileViewModel = hiltViewModel()
                FriendsProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    userListViewModel = viewModel,
                    friendsId = backStackEntry.arguments?.getString("friendsId")!!,
                    navigateToFriends = {
                        navController.navigate(Screen.FriendsFriendsInfoScreen.route)
                    }
                ) {friendListId->
                    navController.navigate(Screen.FriendsWatchListDetailedView.route + "/$friendListId")
                }
            }

        }
    }


}