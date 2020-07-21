package com.orbital.snus.messages.Messaging

import com.orbital.snus.databinding.MessagesMessagingBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.messages.MessagesActivity
import com.squareup.picasso.Picasso

class MessagingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MessagesActivity).hideNavBar()

        val binding: MessagesMessagingBinding = DataBindingUtil.inflate(
            inflater, R.layout.messages_messaging, container, false
        )

        val userdata = requireArguments().get("userdata") as UserData

        binding.messagesMessagingName.text = userdata.fullname
        Picasso.get().load(userdata.picUri).into(binding.messagesFriendsRecyclerPhotoActual)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MessagesActivity).showNavBar()
    }
}