package com.example.storyapps.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapps.api.ApiConfig
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.response.LoginResponse
import com.example.storyapps.response.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreferences): ViewModel() {

    private val _messageText = MutableLiveData<String>()
    val messageText: LiveData<String> = _messageText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(UserModel(user.name, user.token, user.isLogin))
        }
    }
    fun getUser(): LiveData<UserModel>{
        return pref.getUser().asLiveData()
    }

    fun login(){
        viewModelScope.launch {
            pref.login()
        }
    }


    fun getUserLogin(email: String, password: String){
        val client = ApiConfig.getApiService().loginUser(email, password)
        _isLoading.value = true
        client.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if ((responseBody != null) && !responseBody.error) {
                        val loginUser = responseBody.loginResult
                        saveUser(UserModel(
                            loginUser.name,
                            loginUser.token,
                            loginUser.isLogin))
                        login()
                        _messageText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    if (responseBody != null) {
                        val errorLogin =
                            Gson().fromJson(responseBody.string(), LoginResponse::class.java)
                        _messageText.value = errorLogin.message
                        Log.e("loginViewModel", "onFailure: ${errorLogin.message}")
                    } else {
                        _messageText.value = response.message()
                        Log.e("loginViewModel", "onFailure: ${response.message()}")
                    }

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("loginViewModel", "onFailure: ${t.message}")
            }

        } )
    }
}