package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.auth.User
import com.orbital.snus.R
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.ActivityDashboardBinding
import com.orbital.snus.groups.GroupsActivity
import com.orbital.snus.messages.MessagesActivity
import com.orbital.snus.modules.ModulesActivity
import com.orbital.snus.opening.MainActivity
import com.orbital.snus.profile.ProfileActivity
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityDashboardBinding

    val viewModel: EventViewModel by this.viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        // If no user logged in, transfer to opening screen
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        binding = DataBindingUtil.setContentView<ActivityDashboardBinding>(this, R.layout.activity_dashboard)

        viewModel.loadUsers()
        viewModel.getUsers().observe(this, androidx.lifecycle.Observer<List<UserEvent>> { events ->
            if (events.size != 0) {
                Toast.makeText(this.applicationContext, "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.applicationContext, "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })

        // Bottom Navigation Menu Handler
        binding.bottomNavigationMenu.menu.findItem(R.id.ic_action_home).setChecked(true)
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener {
            menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.ic_action_home -> {}
                R.id.ic_action_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    finish()
                }
                R.id.ic_action_messages -> {
                    startActivity(Intent(applicationContext, MessagesActivity::class.java))
                    finish()
                }
                R.id.ic_action_groups -> {
                    startActivity(Intent(applicationContext, GroupsActivity::class.java))
                    finish()
                }
                R.id.ic_action_modules -> {
                    startActivity(Intent(applicationContext, ModulesActivity::class.java))
                    finish()
                }
            }
            true
        }
    }

    // For Bottom Navigation Menu

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun hideNavBar() {
        binding.bottomNavigationMenu.visibility = View.GONE
    }

    fun showNavBar() {
        binding.bottomNavigationMenu.visibility = View.VISIBLE
    }

}