package com.orbital.snus.profile.MainFriends.MainTimeline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainFriendsRequestBinding
import com.orbital.snus.profile.MainTimeline.MainFriendsAdapter
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModel
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModelFactory


class MainFriendsRequestFragment : Fragment() {
    private lateinit var binding: ProfileMainFriendsRequestBinding
    private lateinit var viewModel: MainFriendsViewModel
    private lateinit var factory: MainFriendsViewModelFactory

    private val requests = ArrayList<UserData>()

    // the recyclerview here should display the user's friends
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView

    val firestore = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_main_friends_request, container, false)

        val _userDataObserver = MutableLiveData<Boolean>()
        firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                userData = it.toObject((UserData::class.java))!!
                _userDataObserver.value = true
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
            }

        _userDataObserver.observe(viewLifecycleOwner, Observer {
            if (it) {

                factory = MainFriendsViewModelFactory(userData)
                viewModel = ViewModelProvider(this, factory).get(MainFriendsViewModel::class.java)

                viewManager = LinearLayoutManager(activity)
                viewAdapter = MainFriendsRequestAdapter(requests)
                recyclerView = binding.recyclerviewRequests.apply {
                    layoutManager = viewManager
                    adapter = viewAdapter
                }

                viewModel.requests.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                        users ->
                    requests.removeAll(requests)
                    requests.addAll(users)
                    recyclerView.adapter!!.notifyDataSetChanged()
                })


                // end of it
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val _setupObserver = MutableLiveData<Boolean>()
        firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                userData = it.toObject((UserData::class.java))!!
                _setupObserver.value = true
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
            }

        _setupObserver.observe(viewLifecycleOwner, Observer {
            factory = MainFriendsViewModelFactory(userData)
            viewModel = ViewModelProvider(this, factory).get(MainFriendsViewModel::class.java)
            viewModel.loadUsers()
            viewModel.loadRequests()

        })
    }


}