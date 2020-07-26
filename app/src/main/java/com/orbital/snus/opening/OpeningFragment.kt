package com.orbital.snus.opening

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.dashboard.DashboardActivity


import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.FragmentOpeningOpeningBinding

class OpeningFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentOpeningOpeningBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening_opening, container, false)

        binding.progressBar.visibility = View.VISIBLE
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {
            binding.progressBar.visibility = View.GONE
        } else if (firebaseAuth.currentUser != null) {
            val firestore = FirebaseFirestore.getInstance()
            var user: UserData? = null

            firebaseAuth.fetchSignInMethodsForEmail(firebaseAuth.currentUser!!.email.toString()).addOnCompleteListener {
                if (it.result?.signInMethods.isNullOrEmpty()) {
                    Toast.makeText(this.context, "User is not in use, logging out", Toast.LENGTH_SHORT).show()
                    firebaseAuth.signOut()
                }
            }

            if (!firebaseAuth.currentUser!!.isEmailVerified) {
                Toast.makeText(this.context, "Please verify account and log in", Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
            }

            // extract user data from firestore
            firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    user = it.toObject(UserData::class.java)!!
                    if (user!!.firstTime!!) {
                        firebaseAuth.signOut() // must sign in to do profile setup
                    } else {
                        startActivity(Intent(activity?.applicationContext, DashboardActivity::class.java))
                        activity?.finish()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this.context, "Error: " + it.message, Toast.LENGTH_SHORT).show()
                }

        }


        binding.buttonLogin.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_openingFragment_to_loginFragment)
        }

        binding.buttonSignup.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_openingFragment_to_registerFragment)
        }
        return binding.root
    }
}
