package com.example.storyapps

import com.example.storyapps.response.Story

object DataDummy {

    fun dummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val listStory = Story(
                i.toString(),
                "name $i",
                "description $i",
                "url $i",
                "created + $i",
                42.7843528,
                -73.0059731
            )
            items.add(listStory)
        }
        return items
    }
}