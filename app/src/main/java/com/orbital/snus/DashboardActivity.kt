package com.orbital.snus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var logout_button: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        logout_button = findViewById(R.id.logout_button)
        firebaseAuth = FirebaseAuth.getInstance()

        logout_button.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

    }
}
