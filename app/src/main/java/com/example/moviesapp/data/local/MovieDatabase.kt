package com.example.moviesapp.data.local

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moviesapp.data.local.movie.MovieDao
import com.example.moviesapp.data.local.movie.MovieEntity
import com.example.moviesapp.data.local.movieDetails.MovieDetailsDao
import com.example.moviesapp.data.local.movieDetails.MovieDetailsEntity
import com.example.moviesapp.data.local.searchMovie.SearchMovieDao
import com.example.moviesapp.data.local.searchMovie.SearchMovieEntity
import com.example.moviesapp.data.local.searchSeries.SearchSeriesDao
import com.example.moviesapp.data.local.searchSeries.SearchSeriesEntity
import com.example.moviesapp.data.local.series.SeriesDao
import com.example.moviesapp.data.local.series.SeriesEntity
import com.example.moviesapp.data.local.seriesDetails.SeriesDetailsDao
import com.example.moviesapp.data.local.seriesDetails.SeriesDetailsEntity
import com.example.moviesapp.data.local.userMediaList.MediaListDao
import com.example.moviesapp.data.local.userMediaList.MediaListEntity


//database class
@Database(
    entities = [MovieEntity::class, SeriesEntity::class, MovieDetailsEntity::class, SeriesDetailsEntity::class, MediaListEntity::class, SearchMovieEntity::class, SearchSeriesEntity::class],
    version = 7,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase(){
    abstract val movieDao: MovieDao
    abstract val seriesDao: SeriesDao
    abstract val movieDetailsDao: MovieDetailsDao
    abstract val seriesDetailsDao: SeriesDetailsDao
    abstract val mediaListDao: MediaListDao
    abstract val searchMovieDao: SearchMovieDao
    abstract val searchSeriesDao: SearchSeriesDao


}