package com.example.storyapps.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapps.databinding.ItemStoryBinding
import com.example.storyapps.response.Story
import com.example.storyapps.ui.detail.DetailActivity

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ListStoryViewHolder>(DIFF_CALLBACK) {

    class ListStoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding){
                tvName.text = story.name
                binding.tvDesc.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivStory)
                itemView.setOnClickListener {
                    val detailIntent = Intent(itemView.context, DetailActivity::class.java)
                    detailIntent.putExtra("key_story", story)
                    itemView.context.startActivity(detailIntent)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}