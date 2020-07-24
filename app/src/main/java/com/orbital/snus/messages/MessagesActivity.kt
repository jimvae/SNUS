package com.orbital.snus.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.databinding.ActivityMessagesBinding
import com.orbital.snus.modules.ModulesActivity
import com.orbital.snus.opening.MainActivity
import com.orbital.snus.profile.ProfileActivity

class MessagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMessagesBinding>(this, R.layout.activity_messages)

        binding.bottomNavigationMenu.menu.findItem(R.id.ic_action_messages).setChecked(true)
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.ic_action_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                R.id.ic_action_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                R.id.ic_action_messages -> {

                }

                R.id.ic_action_modules -> {
                    startActivity(Intent(applicationContext, ModulesActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                }
            }
            true
        }


    }

    fun hideNavBar() {
        binding.bottomNavigationMenu.visibility = View.GONE
    }

    fun showNavBar() {
        binding.bottomNavigationMenu.visibility = View.VISIBLE
    }
}
