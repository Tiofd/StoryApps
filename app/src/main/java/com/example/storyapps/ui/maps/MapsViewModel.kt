package com.example.storyapps.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapps.api.ApiConfig
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.response.GetStoriesResponse
import com.example.storyapps.response.Story
import com.example.storyapps.response.UserModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferences): ViewModel() {
    private val _listStory = MutableLiveData<List<Story>>()
    val listStory: LiveData<List<Story>> = _listStory

    fun getStoriesWithLocation(location: Int, token: String) {
        val client = ApiConfig.getApiService().getStoriesWithLocation(location, token)
        client.enqueue(object : Callback<GetStoriesResponse> {
            override fun onResponse(
                call: Call<GetStoriesResponse>,
                response: Response<GetStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _listStory.value = responseBody.listStory
                    }
                } else {
                    Log.e("MapsViewModel", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                Log.e("MapsViewModel", "onFailure2: Gagal")
            }
        })
    }


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}