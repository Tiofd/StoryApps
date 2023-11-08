package com.example.storyapps.ui.addStory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapps.api.ApiConfig
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.response.AddStoryResponse
import com.example.storyapps.response.UserModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val pref: UserPreferences): ViewModel() {
    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText

    private val _hasUploaded = MutableLiveData<File>()
    val hasUploaded: LiveData<File> = _hasUploaded

    fun setFile(value: File) {
        _hasUploaded.value = value
    }

    fun uploadImage(token: String, file: File, description: String) {
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

        val apiService = ApiConfig.getApiService()
        val uploadImageRequest = apiService.addStory(token ,imageMultipart, descriptionRequestBody)

        uploadImageRequest.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _messageText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        val errorAddStory = Gson().fromJson(responseBody.string(), AddStoryResponse::class.java)
                        _messageText.value = errorAddStory.message
                        Log.e("AddStoryViewModel", "onFailure: ${errorAddStory.message}")
                    } else {
                        _messageText.value = response.message()
                        Log.e("AddStoryViewModel", "onFailure: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                Log.e("AddStoryViewModel", "onFailure: ${t.message}")
            }
        })
    }
    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }
}