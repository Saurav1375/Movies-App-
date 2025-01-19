package com.example.moviesapp.presentation.friends_profile_screen


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.presentation.profile_screen.ProfileEvents
import com.example.moviesapp.presentation.profile_screen.UserDataState
import com.example.moviesapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsProfileViewModel @Inject constructor(
    private val repository: UserListRepository,
    private val auth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _friendDataState = MutableStateFlow(FriendProfileState())
    private val _mediaListState = MutableStateFlow<List<MediaList>>(emptyList())
    val mediaList : StateFlow<List<MediaList>> = _mediaListState.asStateFlow()
    val friendsData = combine(
        _friendDataState,
        _mediaListState,
    ) { friendDataState, mediaListState->
        friendDataState.copy(
            userData = friendDataState.userData.copy(mediaLists = mediaListState),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FriendProfileState()
    )

    private val _friendData = MutableStateFlow<List<UserData>>(emptyList())
    val friendData: StateFlow<List<UserData>> = _friendData.asStateFlow()

    private val user = auth.currentUser
    private val friendId = savedStateHandle.get<String>("friendsId")

    init {
        friendId?.let {
            getAllMediaLists(it)
            getUserData(it)
            fetchFriendsData(it)
        }
    }

    fun onEvents(event: FriendsProfileEvents) {
        when (event) {
            is FriendsProfileEvents.OnAddFriend -> {
                addFriend(event.friendsId, event.userId)
            }
        }
    }



    private fun getAllMediaLists(userId: String, forceFetchFromRemote : Boolean = true) {
        viewModelScope.launch {
            _friendDataState.update {
                it.copy(
                    isLoading = true,
                )
            }
            repository.getAllLists(userId, forceFetchFromRemote).collect { result ->
                when (result) {

                    is Resource.Success -> {
                        println(result.data)
                        _mediaListState.value = result.data ?: emptyList()
                        _friendDataState.update {
                            it.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _friendDataState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }

                    is Resource.Loading -> {
                        _friendDataState.update {
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

    private fun getUserData(userId: String) {
        viewModelScope.launch {
            repository.getUserData(userId).collect { result ->

                when (result) {
                    is Resource.Success -> {
                        _friendDataState.update {
                            it.copy(
                                userData = result.data ?: it.userData,
                                isLoading = false,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _friendDataState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )

                        }
                    }

                    is Resource.Loading -> {
                        _friendDataState.update {
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


    private fun addFriend(friendsId: String, userId: String) {
        viewModelScope.launch {
            repository.addFriend(friendsId, userId)
        }

    }

    private fun fetchFriendsData(userId: String) {
        viewModelScope.launch {
            repository.getFriendsData(userId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let {
                            _friendData.value = it
                        }

                    }
                    is Resource.Error -> {
                        _friendData.value = emptyList()
                    }
                    is Resource.Loading -> Unit
                }
            }
        }
    }
}