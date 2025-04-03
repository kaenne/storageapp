package com.example.storageapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {


    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var btnCreateUser: Button


    companion object {
        private const val CREDENTIAL_SHARED_PREF = "our_shared_pref"
        private const val KEY_USERNAME = "Username"
        private const val KEY_PASSWORD = "Password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupCreateUserButton()
    }

    private fun initViews() {
        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        edConfirmPassword = findViewById(R.id.ed_confirm_pwd)
        btnCreateUser = findViewById(R.id.btn_create_user)
    }

    private fun setupCreateUserButton() {
        btnCreateUser.setOnClickListener {
            handleUserCreation()
        }
    }

    private fun handleUserCreation() {
        val username = edUsername.text.toString().trim()
        val password = edPassword.text.toString()
        val confirmPassword = edConfirmPassword.text.toString()

        when {
            username.isEmpty() -> showToast("Please enter username")
            password.isEmpty() -> showToast("Please enter password")
            password != confirmPassword -> showToast("Passwords don't match")
            password.length < 6 -> showToast("Password should be at least 6 characters")
            else -> saveCredentialsAndFinish(username, password)
        }
    }

    private fun saveCredentialsAndFinish(username: String, password: String) {
        getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
        showToast("Account created successfully")
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}