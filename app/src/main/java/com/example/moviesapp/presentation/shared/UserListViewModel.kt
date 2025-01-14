package com.example.moviesapp.presentation.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.presentation.profile_screen.UserDataState
import com.example.moviesapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: UserListRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userDataState = MutableStateFlow(UserDataState())
    val _mediaListState = MutableStateFlow<List<MediaList>>(emptyList())
    val userData = combine(_userDataState, _mediaListState) { userDataState, mediaListState ->
        userDataState.copy(userData = userDataState.userData.copy(mediaLists = mediaListState))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserDataState()
    )
    private val user = auth.currentUser

    init {

        user?.let {
            getAllMediaLists(it.uid)
            getUserData(it.uid)
        }


    }

    fun addUserData(userData: UserData, userId: String) {
        viewModelScope.launch {
            repository.addUserData(userData, userId)
        }
    }

    fun addMediaToWatchListByWatchListId(watchListId: String, media: Media, userId: String) {
        viewModelScope.launch {
            repository.addMediaToListByListId(watchListId, media, userId)
        }

    }

    fun removeMediaFromWatchListByWatchListId(watchListId: String, media: Media, userId: String) {
        viewModelScope.launch {
            repository.removeMediaFromListByListId(watchListId, media, userId)
        }
    }

    fun addWatchList(watchList: MediaList, userId: String) {
        viewModelScope.launch {
            repository.addList(watchList, userId)
        }
    }

    private fun getAllMediaLists(userId: String) {
        viewModelScope.launch {
            _userDataState.update {
                it.copy(
                    isLoading = true,
                )
            }
            repository.getAllLists(userId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _mediaListState.value = result.data ?: emptyList()
                        _userDataState.update {
                            it.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _userDataState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _userDataState.update {
                            it.copy(
                                isLoading = result.isLoading,
                                error = null
                            )
                        }

                    }
                }

            }

        }
    }

    fun getUserData(userId: String) {
        viewModelScope.launch {
            repository.getUserData(userId).collect { result ->

                when (result) {
                    is Resource.Success -> {
                        _userDataState.update {
                            it.copy(
                                userData = result.data ?: it.userData,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _userDataState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )

                        }
                    }

                    is Resource.Loading -> {
                        _userDataState.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                }
            }
        }

    }

}