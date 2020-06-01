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
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.DashboardActivity

import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentOpeningBinding
import com.orbital.snus.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var fullNameText: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentRegisterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        // Buttons
        fullNameText = binding.fullNameText
        emailText = binding.emailText
        passwordText = binding.passwordText
        registerButton = binding.registerButton
        loginButton = binding.regToLogButton

        progressBar = binding.registerProgressBar
        progressBar.visibility = View.GONE

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

            // Set editable fields to be non-editable
            fullNameText.isEnabled = false
            emailText.isEnabled = false
            passwordText.isEnabled = false
            loginButton.isEnabled = false
            registerButton.isEnabled = false

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                        task ->
                    run {
                        if (task.isSuccessful) {
                            Toast.makeText(this.context, "User Created", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(activity?.applicationContext, DashboardActivity::class.java))
                            activity?.finish()
                        } else {
                            Toast.makeText(this.context, "Error: " + (task.exception?.message ?: "Unknown"), Toast.LENGTH_SHORT).show()

                            fullNameText.isEnabled = true
                            emailText.isEnabled = true
                            passwordText.isEnabled = true
                            loginButton.isEnabled = true
                            registerButton.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        loginButton.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }
}
