package com.example.moviesapp.presentation.shared

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.presentation.profile_screen.ProfileEvents
import com.example.moviesapp.presentation.profile_screen.UserDataState
import com.example.moviesapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
class UserListViewModel @Inject constructor(
    private val repository: UserListRepository,
    private val auth: FirebaseAuth,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userDataState = MutableStateFlow(UserDataState())
    private val _queryUsers = MutableStateFlow<List<UserData>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    val _mediaListState = MutableStateFlow<List<MediaList>>(emptyList())
    val userData = combine(
        _userDataState,
        _mediaListState,
        _queryUsers
    ) { userDataState, mediaListState, queryUsers ->
        userDataState.copy(
            userData = userDataState.userData.copy(mediaLists = mediaListState),
            queryUsers = queryUsers
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserDataState()
    )

    private val _friendData = MutableStateFlow<List<UserData>>(emptyList())
    val friendData: StateFlow<List<UserData>> = _friendData.asStateFlow()

    private val user = auth.currentUser
    private var currentJob: Job? = null

    init {

        user?.let {
            val friendsId = savedStateHandle.get<String>("friendsId")
            getAllMediaLists(it.uid)
            getUserData(it.uid)
            fetchFriendsData(it.uid)
        }
    }

    fun onEvents(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.OnSearchQueryChange -> {
                _searchQuery.update {
                    event.query
                }
                currentJob?.cancel()
                currentJob = viewModelScope.launch {
                    delay(300L)
                    searchUsers(_searchQuery.value)
                }
            }

            is ProfileEvents.OnAddFriend -> {
                addFriend(event.friendsId, user?.uid ?: "")
            }
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


    private fun getAllMediaLists(userId: String, forceFetchFromRemote : Boolean = false) {
        viewModelScope.launch {
            _userDataState.update {
                it.copy(
                    isLoading = true,
                )
            }
            repository.getAllLists(userId, forceFetchFromRemote).collect { result ->
                when (result) {

                    is Resource.Success -> {
                        println(result.data)
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

    private fun getUserData(userId: String) {
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


    private fun searchUsers(query: String) {
        viewModelScope.launch {
            repository.searchFriend(query, currentUserId = user?.uid ?: "").collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _queryUsers.update {
                            result.data ?: emptyList()
                        }

                    }

                    is Resource.Error -> {
                        _queryUsers.update {
                            emptyList()
                        }
                    }

                    is Resource.Loading -> {
                        _queryUsers.update {
                            emptyList()
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