package com.example.storageapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var tvData: TextView
    private lateinit var btnWriteFile: Button
    private lateinit var btnReadFile: Button
    private lateinit var btnLogout: Button
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получаем имя пользователя из Intent (исправлено на "USERNAME")
        currentUsername = intent.getStringExtra("USERNAME") ?: "default_user"

        initViews()
        setupButtons()
    }

    private fun initViews() {
        tvData = findViewById(R.id.tvData)
        btnWriteFile = findViewById(R.id.btnWriteFile)
        btnReadFile = findViewById(R.id.btnReadFile)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupButtons() {
        btnWriteFile.setOnClickListener {
            val currentData = "User: $currentUsername\nLast update: ${getCurrentDateTime()}\nHello!\n"
            appendToFile(currentData)  // Теперь данные дописываются в файл
        }

        btnReadFile.setOnClickListener {
            tvData.text = readFromFile()
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getUserFile(): File {
        val userFilesDir = File(filesDir, "user_files")
        if (!userFilesDir.exists()) {
            userFilesDir.mkdirs()
        }
        return File(userFilesDir, "${currentUsername}_data.txt")
    }

    private fun appendToFile(data: String) {
        val file = getUserFile()
        try {
            FileOutputStream(file, true).use { stream ->  // true = дописывание в файл
                stream.write(data.toByteArray())
                showToast("Data saved for $currentUsername")
            }
        } catch (e: Exception) {
            showToast("Error saving file")
            e.printStackTrace()
        }
    }

    private fun readFromFile(): String {
        val file = getUserFile()
        return try {
            if (!file.exists()) return "No data available for $currentUsername"

            FileInputStream(file).use { stream ->
                String(stream.readBytes())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error reading file for $currentUsername"
        }
    }

    private fun logoutUser() {
        getSharedPreferences("AppPrefs", MODE_PRIVATE).edit()
            .putBoolean("is_logged_in", false)
            .apply()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
