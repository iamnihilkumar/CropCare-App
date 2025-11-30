package com.example.cropcare.presentation.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cropcare.R
import com.example.cropcare.presentation.result.ResultActivity

class ScanActivity : AppCompatActivity() {

    private lateinit var ivPreview: ImageView
    private lateinit var btnUpload: Button
    private lateinit var btnProceed: Button
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        ivPreview = findViewById(R.id.ivPreview)
        btnUpload = findViewById(R.id.btnUpload)
        btnProceed = findViewById(R.id.btnProceed)

        btnProceed.isEnabled = false

        btnUpload.setOnClickListener {
            checkStoragePermission()
        }

        btnProceed.setOnClickListener {
            selectedImageUri?.let {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("IMAGE_URI", it.toString())
                startActivity(intent)
            }
        }
    }

    private fun checkStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 101)
        }
    }

    private fun openImagePicker() {
        imagePicker.launch("image/*")
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            ivPreview.setImageURI(uri)
            btnProceed.isEnabled = true
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
}
