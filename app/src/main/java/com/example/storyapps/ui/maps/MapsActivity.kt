package com.example.storyapps.ui.maps

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapps.databinding.ActivityMapsBinding
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.response.Story
import com.example.storyapps.ui.ViewModelFactory
import com.example.storyapps.utils.dataStore
import com.google.android.gms.maps.model.MapStyleOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupViewModel() {
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore)))[MapsViewModel::class.java]
        mapsViewModel.getUser().observe(this) {user->
            supportActionBar?.title = "Hello, ${user.name}!"
            mapsViewModel.getStoriesWithLocation(1,"Bearer ${user.token}")
        }

        mapsViewModel.listStory.observe(this){
            showMarker(it)
        }
    }

    private fun showMarker(listStory: List<Story>) {
        listStory.forEach {
            val latLng = LatLng(it.lat, it.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .snippet(it.description)
                    .title(it.name)
            )
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(ContentValues.TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(ContentValues.TAG, "Can't find style. Error: ", exception)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}