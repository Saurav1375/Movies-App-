package com.example.moviesapp.data.remote

import com.example.moviesapp.BuildConfig
import com.example.moviesapp.data.remote.dto.creditsDto.CreditsDto
import com.example.moviesapp.data.remote.dto.moviedetailsdto.MovieDetailsDto
import com.example.moviesapp.data.remote.dto.moviedto.MovieDto
import com.example.moviesapp.data.remote.dto.seriesdetailsdto.SeriesDetailsDto
import com.example.moviesapp.data.remote.dto.seriesdto.SeriesDto
import com.example.moviesapp.data.remote.dto.videodto.VideoDto
import com.example.moviesapp.domain.model.Credits
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //Movie Details by id
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<MovieDetailsDto>

    //Movie videos by Id
    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<VideoDto>


    //get recommendations of movie by id
    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendations(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ) : Response<MovieDto>


    //Theatre Movies
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ): Response<MovieDto>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ): Response<MovieDto>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ): Response<MovieDto>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ): Response<MovieDto>


    //get recommendations of a series by id
    @GET("tv/{series_id}/recommendations")
    suspend fun getSeriesRecommendations(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ) : Response<SeriesDto>

    //get series videos by id
    @GET("tv/{series_id}/videos")
    suspend fun getSeriesVideos(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<VideoDto>


    @GET("tv/airing_today")
    suspend fun getAiringTodaySeries(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ) : Response<SeriesDto>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirSeries(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ) : Response<SeriesDto>


    @GET("tv/popular")
    suspend fun getPopularSeries(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ) : Response<SeriesDto>

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1
    ) : Response<SeriesDto>


    //get series details by id
    @GET("tv/{series_id}")
    suspend fun getSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String = "en-US",
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ): Response<SeriesDetailsDto>


    //get credits details of movies by id
    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ) : Response<CreditsDto>

    //get credits details of series by id
    @GET("tv/{series_id}/credits")
    suspend fun getSeriesCredits(
        @Path("series_id") seriesId: Int,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY
    ) : Response<CreditsDto>


    //search movies query : title, primary_release_year, page
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("query") query: String,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ) : Response<MovieDto>

    @GET("search/tv")
    suspend fun searchTV(
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("query") query: String,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ) : Response<SeriesDto>


    //get movies by genre id
    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ) : Response<MovieDto>

    //get TV by genre id
    @GET("discover/tv")
    suspend fun getTVByGenre(
        @Query("with_genres") genreId: Int,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ) : Response<SeriesDto>


}