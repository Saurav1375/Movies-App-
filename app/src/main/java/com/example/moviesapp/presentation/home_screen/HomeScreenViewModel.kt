package com.example.moviesapp.presentation.home_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.repository.MediaRepository
import com.example.moviesapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val moviesRepository: MediaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state.asStateFlow()

    private val _mediaType = mutableStateOf(MediaType.MOVIE)

    init {
        getAllMoviesSection(fetchFromRemote = true)
        getAllSeriesSection(fetchFromRemote = true)
    }


    fun onEvents(event: HomeEvents) {
        when (event) {
            is HomeEvents.OnMediaChange -> {
                _mediaType.value = event.mediaType
                _state.update {
                    it.copy(mediaType = event.mediaType)
                }
            }

            is HomeEvents.Refresh -> {
                refreshMovies()
            }

            is HomeEvents.OnLanguageChange -> {
                _state.update {
                    it.copy(language = event.language)
                }
                getAllMoviesSection(event.language)
                getAllSeriesSection(event.language)
            }
        }

    }


    //implementing moviesRepository function to load each section on startup
    private fun getAllMoviesSection(
        language: String = "en-US",
        fetchFromRemote: Boolean = true,
        page: Int = 1,
    ) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)

            try {
                val theatreMovies = async {
                    moviesRepository.getMoviesByType(
                        language,
                        "now_playing",
                        page = page,
                        fetchFromRemote
                    )
                }
                val popularMovies =
                    async {
                        moviesRepository.getMoviesByType(
                            language,
                            "popular",
                            Random.nextInt(1, 6),
                            fetchFromRemote
                        )
                    }
                val upcomingMovies = async {
                    moviesRepository.getMoviesByType(
                        language,
                        "upcoming",
                        Random.nextInt(1, 6),
                        fetchFromRemote
                    )
                }
                val topRatedMovies = async {
                    moviesRepository.getMoviesByType(
                        language,
                        "top_rated",
                        Random.nextInt(1, 6),
                        fetchFromRemote
                    )
                }

                // Collect all movie sections
                theatreMovies.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                movieType = state.value.movieType.copy(
                                    theatreMovie = result.data ?: emptyList()
                                ),
                                isLoading = false
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }

                popularMovies.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                movieType = state.value.movieType.copy(
                                    popularMovies = result.data ?: emptyList()
                                ),
                                isLoading = false
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }

                upcomingMovies.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                movieType = state.value.movieType.copy(
                                    upcomingMovies = result.data ?: emptyList()
                                ),
                                isLoading = false

                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }

                topRatedMovies.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                movieType = state.value.movieType.copy(
                                    topRatedMovies = result.data ?: emptyList()
                                ),
                                isLoading = false
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                _state.value = state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }


    private fun getAllSeriesSection(
        language: String = "en-US",
        fetchFromRemote: Boolean = true,
        page: Int = 1,
    ) {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            try {
                val airingTodaySeries = async {
                    moviesRepository.getSeriesByType(
                        language,
                        "airing_today",
                        Random.nextInt(1, 6),
                        fetchFormRemote = fetchFromRemote
                    )
                }

                val onTheAirSeries = async {
                    moviesRepository.getSeriesByType(
                        language,
                        "on_the_air",
                        Random.nextInt(1, 6),
                        fetchFormRemote = fetchFromRemote
                    )
                }

                val popularSeries = async {
                    moviesRepository.getSeriesByType(
                        language,
                        "popular",
                        Random.nextInt(1, 6),
                        fetchFormRemote = fetchFromRemote
                    )
                }

                val topRatedSeries = async {
                    moviesRepository.getSeriesByType(
                        language,
                        "top_rated",
                        Random.nextInt(1, 3),
                        fetchFormRemote = fetchFromRemote
                    )
                }


                airingTodaySeries.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                seriesType = state.value.seriesType.copy(
                                    airingTodaySeries = result.data ?: emptyList()
                                ),
                                isLoading = false
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false

                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }

                onTheAirSeries.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                seriesType = state.value.seriesType.copy(
                                    onTheAirSeries = result.data ?: emptyList(),

                                    ),
                                isLoading = false
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false


                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading,

                                )
                        }
                    }
                }

                popularSeries.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                seriesType = state.value.seriesType.copy(
                                    popularSeries = result.data ?: emptyList(),
                                ),
                                isLoading = false

                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false

                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }

                topRatedSeries.await().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                seriesType = state.value.seriesType.copy(
                                    topRatedSeries = result.data ?: emptyList()
                                ),
                                isLoading = false

                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false

                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = result.isLoading
                            )
                        }
                    }
                }


            } catch (e: Exception) {
                _state.value = state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }


    // Add a function to refresh the data
    private fun refreshMovies() {
        _state.value = state.value.copy(isRefreshing = true)
        if (_state.value.mediaType == MediaType.MOVIE) getAllMoviesSection(fetchFromRemote = true)
        else getAllSeriesSection(fetchFromRemote = true)

        _state.value = state.value.copy(isRefreshing = false)
    }

}