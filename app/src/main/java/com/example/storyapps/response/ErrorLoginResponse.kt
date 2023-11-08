package com.example.storyapps.response

import com.google.gson.annotations.SerializedName

class ErrorLoginResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
    )