package com.example.storyapps.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapps.databinding.ActivityDetailBinding
import com.example.storyapps.response.Story


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDetail()


    }
    private fun getDetail(){
        @Suppress("DEPRECATION")
        val getIntent = intent.getParcelableExtra<Story>("key_story") as Story
        binding.tvDetailName.text = getIntent.name
        binding.tvDetailDesc.text = getIntent.description
        Glide.with(this)
            .load(getIntent.photoUrl)
            .into(binding.ivDetailPhoto)
    }
}