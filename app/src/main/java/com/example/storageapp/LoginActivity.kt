package com.example.storageapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    // Views
    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    // Constants
    companion object {
        private const val CREDENTIAL_SHARED_PREF = "our_shared_pref"
        private const val USERS_LIST_KEY = "users_list"
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
        val usersJson = credentials.getString(USERS_LIST_KEY, "[]") ?: "[]"
        val usersList = JSONArray(usersJson)

        val inputUsername = edUsername.text.toString().trim()
        val inputPassword = edPassword.text.toString().trim()

        if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
            showToast("Please enter both username and password")
            return
        }

        var loginSuccess = false

        for (i in 0 until usersList.length()) {
            val userObj = usersList.getJSONObject(i)
            val savedUsername = userObj.getString(KEY_USERNAME)
            val savedPassword = userObj.getString(KEY_PASSWORD)

            if (savedUsername.equals(inputUsername, ignoreCase = true) &&
                isPasswordValid(savedPassword, inputPassword)) {

                loginSuccess = true
                break
            }
        }

        if (loginSuccess) {
            showToast("Login Successful")
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("USERNAME", inputUsername)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        } else {
            showToast("Invalid username or password")
        }
    }

    private fun isPasswordValid(savedPassword: String, inputPassword: String): Boolean {
        return hashPassword(inputPassword) == savedPassword
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
