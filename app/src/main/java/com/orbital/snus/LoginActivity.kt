package com.orbital.snus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //Buttons
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()

        //Buttons
        emailText = binding.emailText
        passwordText = binding.passwordText
        loginButton = binding.loginButton
        registerButton = binding.logToRegButton

        progressBar = binding.loginProgressBar
        progressBar.visibility = View.GONE

        // If logged in, connect to dashboard
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            // User authentication
            if (TextUtils.isEmpty(email)) {
                emailText.setError("Email is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordText.setError("Password is required")
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordText.setError("Password must be at least 6 characters long")
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            // Set editable fields to be non-editable
            emailText.isEnabled = false
            passwordText.isEnabled = false
            loginButton.isEnabled = false
            registerButton.isEnabled = false

            // Sign in with firebase
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    kotlin.run {
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, DashboardActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error: " + (task.exception?.message ?: "Unknown"), Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE

                            emailText.isEnabled = true
                            passwordText.isEnabled = true
                            loginButton.isEnabled = true
                            registerButton.isEnabled = true
                        }
                    }
                }

            // hide the keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        // Switch to RegisterActivity
        registerButton.setOnClickListener{
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }
    }

    // On Back, go back to opening screen
    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }


}
