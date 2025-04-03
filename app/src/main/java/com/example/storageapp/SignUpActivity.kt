package com.example.storageapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

class SignUpActivity : AppCompatActivity() {

    private lateinit var edUsername: EditText
    private lateinit var edPassword: EditText
    private lateinit var edConfirmPassword: EditText
    private lateinit var btnCreateUser: Button
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    companion object {
        private const val CREDENTIAL_SHARED_PREF = "our_shared_pref"
        private const val USERS_LIST_KEY = "users_list"
        private const val KEY_USERNAME = "Username"
        private const val KEY_PASSWORD = "Password"
        private const val MIN_PASSWORD_LENGTH = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupPasswordValidation()
        setupCreateUserButton()
    }

    private fun initViews() {
        edUsername = findViewById(R.id.ed_username)
        edPassword = findViewById(R.id.ed_password)
        edConfirmPassword = findViewById(R.id.ed_confirm_pwd)
        btnCreateUser = findViewById(R.id.btn_create_user)
        tilPassword = findViewById(R.id.til_password)
        tilConfirmPassword = findViewById(R.id.til_confirm_password)
    }

    private fun setupPasswordValidation() {
        edPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
                validatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        edConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePasswordMatch()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validatePassword(): Boolean {
        val password = edPassword.text.toString()
        return when {
            password.isEmpty() -> {
                tilPassword.error = "Enter password"
                false
            }
            password.length < MIN_PASSWORD_LENGTH -> {
                tilPassword.error = "At least $MIN_PASSWORD_LENGTH characters"
                false
            }
            !password.any { it.isLetter() } || !password.any { it.isDigit() } -> {
                tilPassword.error = "Requires letters and numbers"
                false
            }
            else -> {
                tilPassword.error = null
                true
            }
        }
    }

    private fun validatePasswordMatch(): Boolean {
        val password = edPassword.text.toString()
        val confirmPassword = edConfirmPassword.text.toString()

        return if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            tilConfirmPassword.error = "Passwords don't match"
            false
        } else {
            tilConfirmPassword.error = null
            true
        }
    }

    private fun setupCreateUserButton() {
        btnCreateUser.setOnClickListener {
            if (validateForm()) {
                saveCredentialsAndFinish()
            }
        }
    }

    private fun validateForm(): Boolean {
        val usernameValid = edUsername.text.toString().isNotEmpty()
        val passwordValid = validatePassword()
        val passwordMatchValid = validatePasswordMatch()

        if (!usernameValid) {
            showToast("Please enter username")
        }

        return usernameValid && passwordValid && passwordMatchValid
    }

    private fun saveCredentialsAndFinish() {
        val username = edUsername.text.toString().trim()
        val password = edPassword.text.toString()

        val sharedPreferences = getSharedPreferences(CREDENTIAL_SHARED_PREF, Context.MODE_PRIVATE)
        val usersJson = sharedPreferences.getString(USERS_LIST_KEY, "[]") ?: "[]"
        val usersList = JSONArray(usersJson)

        // Проверяем, есть ли уже такой пользователь
        for (i in 0 until usersList.length()) {
            val userObj = usersList.getJSONObject(i)
            if (userObj.getString(KEY_USERNAME) == username) {
                showToast("Username already exists")
                return
            }
        }

        // Хешируем пароль перед сохранением
        val hashedPassword = hashPassword(password)

        // Добавляем нового пользователя
        val newUser = JSONObject().apply {
            put(KEY_USERNAME, username)
            put(KEY_PASSWORD, hashedPassword)
        }
        usersList.put(newUser)

        // Сохраняем обновленный список пользователей
        sharedPreferences.edit().putString(USERS_LIST_KEY, usersList.toString()).apply()

        showToast("Account created successfully")
        finish()
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
