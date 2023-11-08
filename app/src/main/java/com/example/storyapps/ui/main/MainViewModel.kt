package com.example.storyapps.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.response.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreferences): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}