package com.orbital.snus.messages.MainPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.orbital.snus.R
import com.orbital.snus.databinding.MessagesMainPageBinding
import com.orbital.snus.messages.MessagesActivity

class MessagesMainPageFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: MessagesMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.messages_main_page, container, false
        )

        binding.messagesMainAddMessage.setOnClickListener {
            findNavController().navigate(R.id.action_messagesMainPageFragment_to_messagesNewMessageFragment)
        }

        return binding.root
    }


}