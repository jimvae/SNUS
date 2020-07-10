package com.orbital.snus.profile.MainFriends.MainTimeline

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainFriendsSearchBinding
import com.orbital.snus.profile.MainTimeline.MainFriendsAdapter
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModel
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [MainFriendsSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFriendsSearchFragment : Fragment() {

    private lateinit var binding: ProfileMainFriendsSearchBinding
    private lateinit var viewModel: MainFriendsViewModel
    private lateinit var factory: MainFriendsViewModelFactory

    private val filteredUsers = ArrayList<UserData>()

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.profile_main_friends_search, container, false)
        binding.textSearchTitle.text = binding.textSearchTitle.text.toString() + "\n${requireArguments().get("search").toString()}"

        factory = MainFriendsViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(MainFriendsViewModel::class.java)

        viewManager = LinearLayoutManager(activity)
        viewAdapter = MainFriendsAdapter(filteredUsers)
        recyclerView = binding.recyclerviewSearch.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.filteredUsers.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                users ->
            filteredUsers.removeAll(filteredUsers)
            filteredUsers.addAll(users)
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val text: String = requireArguments().get("search").toString()
        viewModel.filterUsers(text)
        viewModel.filteredUsers.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<UserData>> { users ->
            if (users.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }
}