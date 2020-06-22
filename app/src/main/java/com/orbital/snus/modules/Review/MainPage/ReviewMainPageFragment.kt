package com.orbital.snus.modules.Review.MainPageFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.dashboard.Today.TodayEventAdapter
import com.orbital.snus.dashboard.Today.TodayViewModel
import com.orbital.snus.dashboard.Today.TodayViewModelFactory
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardTodayBinding
import com.orbital.snus.databinding.ModuleForumMainPageBinding
import com.orbital.snus.databinding.ModuleReviewMainPageBinding
import java.util.ArrayList
import java.util.Observer

class ReviewMainPageFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    private val mods = ArrayList<String>() // holder to store events and for RecyclerViewAdapter to observe

    // on main page, load out all the user's modules
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleReviewMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_main_page, container, false
        )
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_reviewMainPageFragment_to_individualModuleFragment2)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}