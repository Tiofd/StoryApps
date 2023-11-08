package com.example.storyapps.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapps.api.ApiService
import com.example.storyapps.response.Story

class StoryPagingSource(private val token: String, private val apiService: ApiService) :
    PagingSource<Int, Story>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories("Bearer $token", position, params.loadSize)
            Log.d("pager", "$responseData")
            return LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}