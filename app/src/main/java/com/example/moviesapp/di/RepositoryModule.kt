package com.example.moviesapp.di

import com.example.moviesapp.data.repository.AuthRepositoryImpl
import com.example.moviesapp.data.repository.MediaDetailsRepositoryImpl
import com.example.moviesapp.data.repository.MediaRepositoryImpl
import com.example.moviesapp.data.repository.SearchMediaRepositoryImpl
import com.example.moviesapp.data.repository.UserListRepositoryImpl
import com.example.moviesapp.data.repository.WatchGuideRepositoryImpl
import com.example.moviesapp.domain.repository.AuthRepository
import com.example.moviesapp.domain.repository.MediaDetailsRepository
import com.example.moviesapp.domain.repository.MediaRepository
import com.example.moviesapp.domain.repository.SearchMediaRepository
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.domain.repository.WatchGuideRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMovieRepository(movieRepositoryImpl: MediaRepositoryImpl): MediaRepository

    @Binds
    @Singleton
    abstract fun bindMediaDetailsRepository(mediaDetailsRepositoryImpl: MediaDetailsRepositoryImpl): MediaDetailsRepository

    @Binds
    @Singleton
    abstract fun bindUserListsRepository(userListRepositoryImpl: UserListRepositoryImpl): UserListRepository

    @Binds
    @Singleton
    abstract fun bindWatchGuideRepository(watchGuideRepositoryImpl: WatchGuideRepositoryImpl): WatchGuideRepository

    @Binds
    @Singleton
    abstract fun searchMediaRepository(searchMediaRepositoryImpl: SearchMediaRepositoryImpl): SearchMediaRepository
}