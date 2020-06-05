package com.orbital.snus.modules

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.databinding.ActivityGroupsBinding
import com.orbital.snus.databinding.ActivityModulesBinding
import com.orbital.snus.groups.GroupsActivity
import com.orbital.snus.messages.MessagesActivity
import com.orbital.snus.profile.ProfileActivity

class ModulesActivity : AppCompatActivity() {

    private lateinit var binding:ActivityModulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modules)
        binding = DataBindingUtil.setContentView<ActivityModulesBinding>(this, R.layout.activity_modules)

        binding.bottomNavigationMenu.menu.findItem(R.id.ic_action_modules).setChecked(true)
        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.ic_action_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    finish()
                }
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

                }
            }
            true
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
