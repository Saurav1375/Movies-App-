package com.example.moviesapp.presentation.watchguide_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.MovieGenre
import com.example.moviesapp.domain.model.SeriesGenre
import com.example.moviesapp.domain.repository.SearchMediaRepository
import com.example.moviesapp.domain.repository.WatchGuideRepository
import com.example.moviesapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchGuideViewmodel @Inject constructor(
    private val repository: WatchGuideRepository,
    private val searchMediaRepository: SearchMediaRepository
) : ViewModel() {

    private val _watchGuideState = MutableStateFlow(WatchGuideState())
    val watchGuideState: StateFlow<WatchGuideState> = _watchGuideState.asStateFlow()
    private val _mediaType = mutableStateOf(MediaType.MOVIE)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchMovieResults = searchQuery
        .debounce(300L)
        .filter { it.length >= 2 }
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                searchMediaRepository.getMovieSearchResultStream(query, "en-US", false)
            }
        }
        .cachedIn(viewModelScope)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchSeriesResults = searchQuery
        .debounce(300L)
        .filter { it.length >= 2 }
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                searchMediaRepository.getSeriesSearchResultStream(query, "en-US", false)
            }
        }
        .cachedIn(viewModelScope)


    init {
        fetchMoviesByGenre(fetchFromRemote = true)
        fetchSeriesByGenre(fetchFromRemote = true)
    }

    fun onEvents(event: WatchGuideEvents) {
        when (event) {
            is WatchGuideEvents.OnMediaChange -> {
                _mediaType.value = event.mediaType
                _watchGuideState.update {
                    it.copy(mediaType = event.mediaType)
                }
            }

            is WatchGuideEvents.Refresh -> {
                fetchMoviesByGenre(fetchFromRemote = true)
                fetchSeriesByGenre(fetchFromRemote = true)
            }

            is WatchGuideEvents.OnSearchQueryChange -> {
                _searchQuery.value = event.query

            }

        }

    }

    private fun fetchMoviesByGenre(
        genreIds: List<Int> = MovieGenre.entries.map { it.id },
        language: String = "en-US",
        page: Int = 1,
        includeAdult: Boolean = true,
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _watchGuideState.update { it.copy(isLoading = true) }

                val movieFetches = genreIds.map { genreId ->
                    async {
                        repository.getMoviesByGenre(
                            genreId = genreId,
                            language = language,
                            page = page,
                            includeAdult = includeAdult,
                            fetchFromRemote = fetchFromRemote
                        ).toList()
                    }
                }

                val allMovies = movieFetches.flatMap { fetch ->
                    fetch.await().mapNotNull { result ->
                        when (result) {
                            is Resource.Success -> result.data
                            is Resource.Error -> {
                                _watchGuideState.update { state ->
                                    state.copy(error = result.message)
                                }
                                null
                            }

                            is Resource.Loading -> {
                                _watchGuideState.update { state ->
                                    state.copy(isLoading = result.isLoading)
                                }
                                null
                            }
                        }
                    }.flatten()
                }

                _watchGuideState.update {
                    it.copy(
                        movieGenre = allMovies,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _watchGuideState.update {
                    it.copy(
                        error = e.localizedMessage ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun fetchSeriesByGenre(
        genreIds: List<Int> = SeriesGenre.entries.map { it.id },
        language: String = "en-US",
        page: Int = 1,
        includeAdult: Boolean = true,
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _watchGuideState.value = watchGuideState.value.copy(isLoading = true)

                val seriesFetches = genreIds.map { genreId ->
                    async {
                        repository.getSeriesByGenre(
                            genreId = genreId,
                            language = language,
                            page = page,
                            includeAdult = includeAdult,
                            fetchFromRemote = fetchFromRemote
                        ).toList() // Convert the flow to a list to collect all emitted items
                    }
                }

                val allSeries = seriesFetches.flatMap { fetch ->
                    fetch.await().mapNotNull { result ->
                        when (result) {
                            is Resource.Success -> result.data
                            is Resource.Error -> {
                                _watchGuideState.value = watchGuideState.value.copy(
                                    error = result.message,
                                    isLoading = false
                                )
                                null
                            }

                            is Resource.Loading -> {
                                _watchGuideState.value = watchGuideState.value.copy(
                                    isLoading = result.isLoading
                                )
                                null
                            }
                        }
                    }.flatten()
                }

                _watchGuideState.value = watchGuideState.value.copy(
                    seriesGenre = allSeries,
                    isLoading = false
                )
            } catch (e: Exception) {
                _watchGuideState.value = watchGuideState.value.copy(
                    error = e.localizedMessage ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }



}