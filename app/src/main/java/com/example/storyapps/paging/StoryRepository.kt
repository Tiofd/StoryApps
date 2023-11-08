package com.example.storyapps.paging
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapps.api.ApiService
import com.example.storyapps.database.StoryDatabase
import com.example.storyapps.response.Story

class StoryRepository(private val storiesDatabase: StoryDatabase,
                      private val apiService: ApiService
) {
    fun getStory(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(token, apiService)
            }
        ).liveData
    }
}