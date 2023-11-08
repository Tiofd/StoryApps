package com.example.storyapps.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapps.paging.StoryRepository
import com.example.storyapps.di.Injection
import com.example.storyapps.response.Story


class PagingViewModel(private val repository: StoryRepository): ViewModel() {

    private val _stories = MutableLiveData<PagingData<Story>>()

    fun story(token: String): LiveData<PagingData<Story>> {
        val response = repository.getStory(token).cachedIn(viewModelScope)
        _stories.value = response.value
        return response
    }

}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PagingViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}