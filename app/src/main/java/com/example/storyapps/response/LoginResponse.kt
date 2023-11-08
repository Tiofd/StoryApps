package com.example.storyapps.response

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    @field:SerializedName("loginResult")
    val loginResult: UserModel,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
    )