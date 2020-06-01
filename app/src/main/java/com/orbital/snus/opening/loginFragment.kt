package com.orbital.snus.opening

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.DashboardActivity

import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentLoginBinding
import com.orbital.snus.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass.
 */
class loginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    //Buttons
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false)
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
            //TODO("Navigate from RegisterFragment to DashboardActivity if user is logged in")
//            startActivity(Intent(this.context, DashboardActivity::class.java))
//            finish()
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
                            Toast.makeText(this.context, "Login Successful", Toast.LENGTH_SHORT).show()
                            //TODO("Navigate from LoginFragment to DashboardActivity if user is logged in")
//                            startActivity(Intent(applicationContext, DashboardActivity::class.java))
//                            finish()
                        } else {
                            Toast.makeText(this.context, "Error: " + (task.exception?.message ?: "Unknown"), Toast.LENGTH_SHORT).show()

                            emailText.isEnabled = true
                            passwordText.isEnabled = true
                            loginButton.isEnabled = true
                            registerButton.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    }
                }

            //TODO("Hide the keyboard")
            // hide the keyboard
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        // Switch to RegisterActivity
        registerButton.setOnClickListener{
            view: View -> view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    //    // On back, go to opening screen
//    override fun onBackPressed() {
//        startActivity(Intent(applicationContext, MainActivity::class.java))
//        finish()
//    }

}