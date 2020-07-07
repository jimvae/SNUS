package com.orbital.snus.opening

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentOpeningRegisterBinding
import kotlinx.android.synthetic.main.fragment_opening_register.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var confirmPassword: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentOpeningRegisterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening_register, container, false
        )

        firebaseAuth = FirebaseAuth.getInstance()

        // Buttons
        confirmPassword = binding.confirmPasswordText
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
            val confirmPasswordText = confirmPassword.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                emailText.setError("Email is required")
                return@setOnClickListener
            }

            val formatEmail = email.trim()
            val domain = formatEmail.split('@').last()
            if (domain != "u.nus.edu" && domain != "nus.edu.sg") {
                emailText.setError("Please use your NUS email")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordText.setError("Password is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPasswordText)) {
                passwordText.setError("Confirm Password is required")
                return@setOnClickListener
            }

            if (password.equals(confirmPasswordText) == false) {
                confirmPassword.setError("Password does not match")
                return@setOnClickListener
            }

            if (password.length < 6) {
                passwordText.setError("Password must be at least 6 characters long")
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            // Set editable fields to be non-editable
            confirmPassword.isEnabled = false
            emailText.isEnabled = false
            passwordText.isEnabled = false
            loginButton.isEnabled = false
            registerButton.isEnabled = false

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    run {
                        if (task.isSuccessful) {
                            firestore = FirebaseFirestore.getInstance()

                            val user: FirebaseUser = firebaseAuth.currentUser!!
                            val userData = UserData(user.uid, null, null, null, null,
                                null, null, null, null, true)
                            firestore.collection("users").document(user.uid).set(userData)

                            // send verification email and direct to login page
                            user.sendEmailVerification().addOnSuccessListener(
                                OnSuccessListener {
                                    Toast.makeText(this@RegisterFragment.context, "Verification Email Has been Sent, Please verify to log-in", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                }
                            ).addOnFailureListener(
                                OnFailureListener {
                                    Toast.makeText(this.requireContext(), "Unable to send: " + it.message, Toast.LENGTH_LONG).show()
                                    Log.d("Email Verification", "Email not Sent")
                                })

                        } else {
                            Toast.makeText(
                                this.context,
                                "Error: " + (task.exception?.message ?: "Unknown"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // reactivate everything, because either error in run or error in sending email
                        confirmPassword.isEnabled = true
                        emailText.isEnabled = true
                        passwordText.isEnabled = true
                        loginButton.isEnabled = true
                        registerButton.isEnabled = true
                        progressBar.visibility = View.GONE
                    }
                }
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        loginButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }
}
