package com.example.moviesapp.domain.model

data class MediaRoom(
    val roomId: String = "",
    val mediaId: Int = 0,
    val mediaType: String = MediaType.MOVIE.name,
    val messages: List<Message> = emptyList()
)

data class Message(
    val id: String = "",
    val senderId : String = "",
    val senderName: String = "",
    val senderProfilePicture: String = "",
    val likes: MutableList<String> = mutableListOf(),
    val text: String = "",
    val timestamp: Long = 0L,
)