package com.example.moviesapp.presentation.profile_screen

sealed class ProfileEvents {
    data class OnSearchQueryChange(val query : String) : ProfileEvents()
    class OnAddFriend(val friendsId : String, val userId: String) : ProfileEvents()
}