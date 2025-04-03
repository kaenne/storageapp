package com.example.storageapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    // Views
    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    // Constants
    companion object {
        private const val CREDENTIAL_SHARED_PREF = "our_shared_pref"
        private const val KEY_USERNAME = "Username"
        private const val KEY_PASSWORD = "Password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        btnLogin = findViewById(R.id.btn_login)
        btnSignUp = findViewById(R.id.btn_signup)
    }

    private fun setupListeners() {
        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    private fun handleLogin() {
        val credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        val savedUsername = credentials.getString(KEY_USERNAME, null)
        val savedPassword = credentials.getString(KEY_PASSWORD, null)
        val inputUsername = edUsername.text.toString().trim()
        val inputPassword = edPassword.text.toString().trim()

        when {
            inputUsername.isEmpty() || inputPassword.isEmpty() -> {
                showToast("Please enter both username and password")
            }
            savedUsername == null || savedPassword == null -> {
                showToast("No credentials found. Please sign up first.")
            }
            !savedUsername.equals(inputUsername, ignoreCase = true) -> {
                showToast("Invalid username")
            }
            !savedPassword.equals(inputPassword, ignoreCase = true) -> {
                showToast("Invalid password")
            }
            else -> {
                showToast("Login Successful")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}