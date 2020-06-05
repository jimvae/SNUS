package com.orbital.snus.groups

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.databinding.ActivityDashboardBinding
import com.orbital.snus.databinding.ActivityGroupsBinding
import com.orbital.snus.messages.MessagesActivity
import com.orbital.snus.modules.ModulesActivity
import com.orbital.snus.profile.ProfileActivity

class GroupsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityGroupsBinding>(this, R.layout.activity_groups)

        binding.bottomNavigationMenu.menu.findItem(R.id.ic_action_groups).setChecked(true)
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
                    startActivity(Intent(applicationContext, MessagesActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                R.id.ic_action_groups -> {
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
}
