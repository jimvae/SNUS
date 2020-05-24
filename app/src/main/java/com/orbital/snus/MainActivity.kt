package com.orbital.snus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var signUp: Button
    private lateinit var logIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signUp = findViewById(R.id.button_signup)
        logIn = findViewById(R.id.button_login)

        logIn.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }

        signUp.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            finish()
        }
    }
}
