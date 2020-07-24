package com.orbital.snus.modules

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.databinding.ActivityModulesBinding
import com.orbital.snus.messages.MessagesActivity
import com.orbital.snus.profile.ProfileActivity


class ModulesActivity : AppCompatActivity() {

    private lateinit var binding:ActivityModulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_modules)
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

    fun hideNavBar() {
        binding.bottomNavigationMenu.visibility = View.GONE
    }

    fun showNavBar() {
        binding.bottomNavigationMenu.visibility = View.VISIBLE
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
