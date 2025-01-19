package com.example.moviesapp.presentation.mediadetails_screen

import com.example.moviesapp.domain.model.UserData

sealed class MediaDetailsEvent {
    data object OnAddClick : MediaDetailsEvent()
    data object OnRefresh : MediaDetailsEvent()

    data class OnSendMessage(val message: String, val userData: UserData) : MediaDetailsEvent()
    data class OnDeleteMessage(val messageId: String) : MediaDetailsEvent()
    data class OnLikedButtonClicked(val messageId: String) : MediaDetailsEvent()
    data class OnUpdateMessage(val messageId: String, val newText: String) : MediaDetailsEvent()
    data class OnAddReaction(val messageId: String, val emoji: String) : MediaDetailsEvent()
    data class OnRemoveReaction(val messageId: String, val emoji: String) : MediaDetailsEvent()

    data class OnCreateWatchList(val watchListName: String) : MediaDetailsEvent()
    data class OnAddToWatchList(val watchListId: String) : MediaDetailsEvent()
}