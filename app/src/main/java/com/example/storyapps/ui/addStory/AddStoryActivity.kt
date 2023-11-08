package com.example.storyapps.ui.addStory
import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.R
import com.example.storyapps.databinding.ActivityAddStoryBinding
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.ui.ViewModelFactory
import com.example.storyapps.ui.main.MainActivity
import com.example.storyapps.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import createCustomTempFile
import reduceFileImage
import rotateImage
import uriToFile
import java.io.File
import java.io.FileOutputStream


class AddStoryActivity : AppCompatActivity() {
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCamera.setOnClickListener { startTakePhoto() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        binding.btnUpload.isEnabled = false

        binding.etAddStory.addTextChangedListener {
            val isDescriptionEmpty = it.isNullOrEmpty()
            val isImageSelected = getFile != null
            binding.btnUpload.isEnabled = !isDescriptionEmpty && isImageSelected
        }

    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.example.storyapps",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                rotateImage(bitmap, currentPhotoPath).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(file)
                )
                addStoryViewModel.setFile(file)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                addStoryViewModel.setFile(myFile)
            }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val description = binding.etAddStory.text.toString()
            val file = reduceFileImage(getFile as File)
            addStoryViewModel.getUser().observe(this){user ->
                addStoryViewModel.uploadImage("Bearer ${user.token}", file, description )
            }
        }
    }
    private fun setupViewModel(){
        addStoryViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore)))[AddStoryViewModel::class.java]
        addStoryViewModel.messageText.observe(this) { text ->
            if (text.contains("success")) {
                Toast.makeText(this, getString(R.string.add_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            else Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
        }
        addStoryViewModel.hasUploaded.observe(this) { file ->
            if (file != null) {
                getFile = file
                binding.imageAddStory.setImageBitmap(BitmapFactory.decodeFile(file.path))
            } else {
                binding.imageAddStory.setImageResource(R.drawable.ic_baseline_image_24)
            }
        }
    }
}
