package com.orbital.snus.modules.Forum.MainPage

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
import java.util.ArrayList
import java.util.Observer

class MainPageFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    val factory = MainPageViewModelFactory()
    private lateinit var viewModel: MainPageViewModel
    private val mods = ArrayList<String>() // holder to store events and for RecyclerViewAdapter to observe

    // on main page, load out all the user's modules
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleForumMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_main_page, container, false
        )
        firestore = FirebaseFirestore.getInstance()

        // click on specific module to go to specific module page
        binding.Dummy.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_mainPageFragment_to_individualModuleFragment)
        }

        viewModel = ViewModelProvider(this, factory).get(MainPageViewModel::class.java)

        viewAdapter = MainPageAdapter(mods)
        viewManager = LinearLayoutManager(activity)

        recyclerView = binding.forumMainPageRecyclerview.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.modules.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<String>> { dbMods ->
            mods.removeAll(mods)
            mods.addAll(dbMods)
            recyclerView.adapter!!.notifyDataSetChanged()
        })



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(MainPageViewModel::class.java)
        viewModel.loadModules()
        viewModel.modules.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<String>> { mods ->
            if (mods.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }

}