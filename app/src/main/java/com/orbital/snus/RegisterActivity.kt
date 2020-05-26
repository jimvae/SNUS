package com.orbital.snus

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var fullNameText: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        firebaseAuth = FirebaseAuth.getInstance()

        fullNameText = binding.fullNameText
        emailText = binding.emailText
        passwordText = binding.passwordText
        registerButton = binding.registerButton
        loginButton = binding.regToLogButton

        progressBar = binding.registerProgressBar
        progressBar.visibility = View.GONE

        // if logged in, transfer to dashboard
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            finish()
        }

        // Creating a new account
        registerButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

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

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                        task ->
                    run {
                        if (task.isSuccessful) {
                            Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, DashboardActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error: " + (task.exception?.message ?: "Unknown"), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        loginButton.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
}
