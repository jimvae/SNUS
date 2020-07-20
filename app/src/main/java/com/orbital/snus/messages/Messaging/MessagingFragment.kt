package com.orbital.snus.messages.Messaging

import com.orbital.snus.databinding.MessagesMessagingBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.orbital.snus.R

class MessagingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: MessagesMessagingBinding = DataBindingUtil.inflate(
            inflater, R.layout.messages_messaging, container, false
        )

        return binding.root
    }
}