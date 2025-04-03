package com.example.storageapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {
    // Views
    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var spinnerUsers: Spinner // Добавляем Spinner для выбора пользователя

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
        loadUsernames() // Загружаем пользователей в Spinner
        setupListeners()
    }

    private fun initViews() {
        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        btnLogin = findViewById(R.id.btn_login)
        btnSignUp = findViewById(R.id.btn_signup)
        spinnerUsers = findViewById(R.id.spinner_users) // Инициализируем Spinner
    }

    private fun loadUsernames() {
        val credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        val usersJson = credentials.getString(USERS_LIST_KEY, "[]") ?: "[]"
        val usersList = JSONArray(usersJson)

        val usernames = mutableListOf<String>()

        for (i in 0 until usersList.length()) {
            val userObj = usersList.getJSONObject(i)
            val username = userObj.getString(KEY_USERNAME)
            usernames.add(username)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usernames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsers.adapter = adapter

        spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                edUsername.setText(usernames[position])
                edPassword.setText(getPasswordForUser(usernames[position]))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getPasswordForUser(username: String): String {
        val credentials = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        val usersJson = credentials.getString(USERS_LIST_KEY, "[]") ?: "[]"
        val usersList = JSONArray(usersJson)

        for (i in 0 until usersList.length()) {
            val userObj = usersList.getJSONObject(i)
            if (userObj.getString(KEY_USERNAME) == username) {
                return userObj.getString(KEY_PASSWORD) // Возвращаем открытый пароль
            }
        }
        return "" // Возвращаем пустую строку, если пользователя не найдено
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
                savedPassword == inputPassword) {

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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
