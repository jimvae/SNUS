package com.orbital.snus.opening

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.dashboard.DashboardActivity


import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentOpeningOpeningBinding

class OpeningFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        // If logged in, connect to dashboard

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(activity?.applicationContext, DashboardActivity::class.java))
            activity?.finish()
        }

        val binding: FragmentOpeningOpeningBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening_opening, container, false)

        binding.buttonLogin.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_openingFragment_to_loginFragment)
        }

        binding.buttonSignup.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_openingFragment_to_registerFragment)
        }
        return binding.root
    }
}
