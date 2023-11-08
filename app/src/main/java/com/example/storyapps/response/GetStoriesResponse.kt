package com.example.storyapps.response

import com.google.gson.annotations.SerializedName

class GetStoriesResponse (
    @field:SerializedName("listStory")
    val listStory: List<Story>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
    )
