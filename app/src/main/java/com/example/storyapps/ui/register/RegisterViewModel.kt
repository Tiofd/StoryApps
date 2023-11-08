package com.example.storyapps.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapps.api.ApiConfig
import com.example.storyapps.response.RegisterResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUserRegister(name: String, email: String, password: String) {
        val client = ApiConfig.getApiService().registerUser(name, email, password)
        _isLoading.value = true
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _messageText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        val errorRegister = Gson().fromJson(responseBody.string(), RegisterResponse::class.java)
                        _messageText.value = errorRegister.message
                        Log.e("RegisterViewModel", "onFailure: ${errorRegister.message}")
                    } else {
                        _messageText.value = response.message()
                        Log.e("RegisterViewModel", "onFailure: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("RegisterViewModel", "onFailure: ${t.message}")
            }
        })
    }
}