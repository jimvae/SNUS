package com.orbital.snus.messages.MainPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.MessagesMainPageBinding
import com.orbital.snus.messages.MessagesActivity
import com.orbital.snus.messages.NewMessage.NewMessageAdapter
import com.orbital.snus.messages.NewMessage.NewMessageViewModel
import com.orbital.snus.messages.NewMessage.NewMessageViewModelFactory
import com.orbital.snus.modules.Forum.MainPage.MainPageViewModelFactory

class MessagesMainPageFragment : Fragment() {
    private lateinit var factory : MessagesMainPageViewModelFactory
    private lateinit var viewModel : MessagesMainPageViewModel
    val users = ArrayList<UserData>()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: MessagesMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.messages_main_page, container, false
        )
        factory = MessagesMainPageViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(MessagesMainPageViewModel::class.java)

        binding.recyclerviewMessageFriendsList.apply {
            adapter = MessagesMainPageAdapter(users, viewModel)
            layoutManager = LinearLayoutManager(this.context)
        }

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            users.removeAll(users)
            users.addAll(it)
            binding.recyclerviewMessageFriendsList.adapter!!.notifyDataSetChanged()
        })


        binding.messagesMainAddMessage.setOnClickListener {
            findNavController().navigate(R.id.action_messagesMainPageFragment_to_messagesNewMessageFragment)
        }

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