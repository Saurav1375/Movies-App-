package com.example.moviesapp.di

import android.app.Application
import androidx.room.Room
import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.remote.ApiService
import com.example.moviesapp.domain.model.UserStateHolder
import com.example.moviesapp.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val database = Firebase.database("https://movieproject-6efb3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database.setPersistenceEnabled(true)
        database.setPersistenceCacheSizeBytes(10 * 1024 * 1024)
        return database
    }

    @Provides
    @Singleton
    fun provideApi(): ApiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(
            MoshiConverterFactory.create(moshi)
        ).build().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieDatabase(context: Application): MovieDatabase {
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    }


}