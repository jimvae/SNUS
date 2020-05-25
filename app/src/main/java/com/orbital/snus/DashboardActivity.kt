package com.orbital.snus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var logout_button: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }

        setContentView(R.layout.activity_dashboard)

        logout_button = findViewById(R.id.logout_button)


        logout_button.setOnClickListener {
            firebaseAuth.addAuthStateListener{
                    firebaseAuth.run {
                        if (firebaseAuth.currentUser == null) {
                            Toast.makeText(
                                this@DashboardActivity,
                                "Sign out successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@DashboardActivity, "Something's wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            firebaseAuth.signOut()
        }

    }
}
