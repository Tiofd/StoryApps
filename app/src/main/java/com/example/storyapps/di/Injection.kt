package com.example.storyapps.di

import android.content.Context
import com.example.storyapps.api.ApiConfig
import com.example.storyapps.database.StoryDatabase
import com.example.storyapps.paging.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}