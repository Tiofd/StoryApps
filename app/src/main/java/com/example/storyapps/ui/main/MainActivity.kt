package com.example.storyapps.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapps.R
import com.example.storyapps.databinding.ActivityMainBinding
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.ui.adapter.LoadingStateAdapter
import com.example.storyapps.ui.adapter.StoryAdapter
import com.example.storyapps.ui.addStory.AddStoryActivity
import com.example.storyapps.ui.login.LoginActivity
import com.example.storyapps.ui.maps.MapsActivity
import com.example.storyapps.utils.dataStore
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val pagingViewModel: PagingViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        clickAction()

    }
    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this,
            com.example.storyapps.ui.ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]
        mainViewModel.getUser().observe(this) { user ->
            supportActionBar?.title = "Hello, ${user.name}!"
            val token = user.token
            showLoading(true)
            getData(token)
            showLoading(false)
        }
    }
    private fun getData(token: String) {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry() }
        )
        binding.rvStory.setHasFixedSize(true)
        lifecycleScope.launch {
            pagingViewModel.story(token).observe(this@MainActivity) {
                adapter.submitData(lifecycle, it)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                logoutAlert()
            }
            R.id.maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun logoutAlert() {
        AlertDialog.Builder(this).apply {
            setMessage("Anda yakin ingin Logout?")
            setPositiveButton("Ya") { _, _ ->
                logout()
            }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }
    private fun logout() {
        mainViewModel.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }
    private fun clickAction(){
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }
}