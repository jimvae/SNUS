package com.orbital.snus.opening

import android.os.Bundle
import android.text.Layout
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator


import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentOpeningBinding
import kotlinx.android.synthetic.main.activity_main.*

class openingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentOpeningBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening, container, false)

        binding.buttonLogin.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_openingFragment_to_loginFragment)
            //Navigation.createNavigateOnClickListener(R.id.action_openingFragment_to_loginFragment)
        }

        binding.buttonSignup.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_openingFragment_to_registerFragment)
        }
        return binding.root
    }
}
