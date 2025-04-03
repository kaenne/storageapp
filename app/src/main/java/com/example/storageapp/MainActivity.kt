package com.example.storageapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var tvData: TextView
    private lateinit var btnWriteFile: Button
    private lateinit var btnReadFile: Button

    // Constants
    companion object {
        private const val REQUEST_CODE_WRITE_PERM = 401
        private const val FILENAME = "storage_app_data.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupButtons()
        requestStoragePermission()
    }

    private fun initViews() {
        tvData = findViewById(R.id.tvData)
        btnWriteFile = findViewById(R.id.btnWriteFile)
        btnReadFile = findViewById(R.id.btnReadFile)
    }

    private fun setupButtons() {
        btnWriteFile.setOnClickListener {
            val currentData = "App data: ${Date(System.currentTimeMillis())}"
            writeToFile(currentData)
        }

        btnReadFile.setOnClickListener {
            tvData.text = readFromFile()
        }
    }

    private fun requestStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                Toast.makeText(this, "Storage permission is needed to save files", Toast.LENGTH_LONG).show()
                requestPermission()
            }
            else -> requestPermission()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_WRITE_PERM
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_PERM -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun writeToFile(data: String) {
        try {
            val file = File(getExternalFilesDir(null), FILENAME)
            FileOutputStream(file).use { stream ->
                stream.write(data.toByteArray())
                Toast.makeText(this, "Data saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun readFromFile(): String {
        return try {
            val file = File(getExternalFilesDir(null), FILENAME)
            if (!file.exists()) return "No data file found"

            FileInputStream(file).use { stream ->
                val bytes = ByteArray(file.length().toInt())
                stream.read(bytes)
                String(bytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error reading file: ${e.message}"
        }
    }
}