package com.example.moviesapp.domain.model

import java.util.UUID

data class UserData(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val appHandle : String = "",
    val profilePictureUrl: String = "",
    val friends : List<String> = emptyList(),
    val followers : List<String> = emptyList(),
    val following : List<String> = emptyList(),
    val mediaLists : List<MediaList> = emptyList()
)


data class MediaList(
    val name : String = "",
    val type : String = "",
    val id : String = UUID.randomUUID().toString(),
    val list : List<Media> = emptyList()
)


data class Media(
    val id : Int = 0,
    val title : String = "",
    val posterPath : String = "",
    val type : String = "",
)
enum class ListType {
    WATCHLIST,
    FAVOURITES,
    WATCHED
}

