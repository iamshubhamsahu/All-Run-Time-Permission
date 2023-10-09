package com.example.makecameraapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var cameraButton: AppCompatButton
    private lateinit var currentPhotoPath: String
    private lateinit var appCompatImageView: AppCompatImageView

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // The photo was captured and saved successfully
                // Display the captured image in your ImageView
                appCompatImageView.setImageURI(Uri.parse(currentPhotoPath))
            } else {
                // Photo capture failed or was canceled
                Toast.makeText(this, "Photo capture failed or was canceled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraButton = findViewById(R.id.btnCamera)
        appCompatImageView = findViewById(R.id.appCompatImageView)
        cameraButton.setOnClickListener {
            if (!hasPermissions(this, *REQUIRED_PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, ALL_PERMISSIONS)
            } else {
                Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({ startCamera() }, 1000)
            }
        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun createTempImageFile(): File {
        val timeStamp: String = System.currentTimeMillis().toString()
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun startCamera() {
        val imageFile = createTempImageFile()
        val photoURI: Uri = FileProvider.getUriForFile(
            this, "com.example.makecameraapplication.provider", imageFile
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        takePictureLauncher.launch(cameraIntent)
    }

    private fun finishActivity() {
        Toast.makeText(
            this, "You must grant all required permissions to continue", Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ALL_PERMISSIONS -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    startCamera()
                } else {
                    finishActivity()
                }
            }
        }
    }

    companion object {
        const val ALL_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BODY_SENSORS,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.GET_ACCOUNTS,
            android.Manifest.permission.NFC,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }
}