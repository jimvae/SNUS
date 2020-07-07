package com.orbital.snus.opening

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.dashboard.DashboardActivity

import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.FragmentOpeningLoginBinding
import kotlinx.android.synthetic.main.fragment_opening_login.*
import kotlinx.android.synthetic.main.module_forum_main_page.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    //Buttons
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var forgotPassword: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentOpeningLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening_login, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        //Buttons
        emailText = binding.emailText
        passwordText = binding.passwordText
        loginButton = binding.loginButton
        registerButton = binding.logToRegButton
        forgotPassword = binding.textForgetPassword

        progressBar = binding.loginProgressBar
        progressBar.visibility = View.GONE


        loginButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            // User authentication
            if (TextUtils.isEmpty(email)) {
                emailText.setError("Email is required")
                return@setOnClickListener
            }

            val formatEmail = email.trim()
            val domain = formatEmail.split('@').last()
            print("Domain is here: " + domain)
            if (domain != "u.nus.edu" && domain != "nus.edu.sg") {
                emailText.setError("Please use your NUS email")
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
                            if (!firebaseAuth.currentUser!!.isEmailVerified) {
                                Toast.makeText(this.context, "Please verify email before logging in", Toast.LENGTH_SHORT).show()
                                firebaseAuth.signOut()
                                return@run
                            }

                            Toast.makeText(this.context, "Login Successful", Toast.LENGTH_SHORT).show()

                            val firestore = FirebaseFirestore.getInstance()
                            var user: UserData? = null
                            // extract user data from firestore
                            firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
                                    .addOnSuccessListener {
                                        user = it.toObject(UserData::class.java)!!
                                        if (user!!.firstTime!!) {
                                            findNavController().navigate(R.id.action_loginFragment_to_profileSetUpFragment)
                                        } else {
                                            startActivity(Intent(activity?.applicationContext, DashboardActivity::class.java))
                                            activity?.finish()
                                        }
                                    }.addOnFailureListener {
                                        Toast.makeText(this.context, "Error: " + it.message, Toast.LENGTH_SHORT).show()
                                    }
                        } else {
                            Toast.makeText(this.context, "Error: " + (task.exception?.message ?: "Unknown"), Toast.LENGTH_SHORT).show()
                        }
                    }
                    emailText.isEnabled = true
                    passwordText.isEnabled = true
                    loginButton.isEnabled = true
                    registerButton.isEnabled = true
                    progressBar.visibility = View.GONE
                }

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        // Switch to RegisterActivity
        registerButton.setOnClickListener{
            view: View -> view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        forgotPassword.setOnClickListener {
            val email = emailText.text.toString().trim()

            // User authentication
            if (TextUtils.isEmpty(email)) {
                emailText.setError("Email is required")
                return@setOnClickListener
            }

            val formatEmail = email.trim()
            val domain = formatEmail.split('@').last()
            print("Domain is here: " + domain)
            if (domain != "u.nus.edu" && domain != "nus.edu.sg") {
                emailText.setError("Please use your NUS email")
                return@setOnClickListener
            }

            firebaseAuth.sendPasswordResetEmail(email.toString()).addOnSuccessListener {
                Toast.makeText(this.requireContext(), "Link to reset password has been sent to your email", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(this.requireContext(), "Unable to send: " + it.message, Toast.LENGTH_LONG).show()
                Log.d("Email Verification", "Email not Sent")

            }
        }

        return binding.root
    }
}