package com.orbital.snus.messages.NewMessage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.MessagesNewMessageBinding


class MessagesNewMessageFragment : Fragment() {

    private lateinit var factory : NewMessageViewModelFactory
    private lateinit var viewModel : NewMessageViewModel

    val users = ArrayList<UserData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding =
            DataBindingUtil.inflate<MessagesNewMessageBinding>(inflater, R.layout.messages_new_message, container, false)

        factory = NewMessageViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(NewMessageViewModel::class.java)

        binding.recyclerviewNewmessage.apply {
            adapter = NewMessageAdapter(users)
            layoutManager = LinearLayoutManager(this.context)
        }

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            users.removeAll(users)
            users.addAll(it)
            binding.recyclerviewNewmessage.adapter!!.notifyDataSetChanged()
        })

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadUsers()
        viewModel.users.observe(viewLifecycleOwner, Observer {
            if (it.size != 0) {
                viewModel.loadFriends()
            }
        })
    }

}