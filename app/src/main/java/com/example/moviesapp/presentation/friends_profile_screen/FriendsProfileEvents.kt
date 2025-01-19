package com.example.moviesapp.presentation.friends_profile_screen

sealed class FriendsProfileEvents {
    class OnAddFriend(val friendsId : String, val userId: String) : FriendsProfileEvents()
}