package com.orbital.snus.messages.MainPage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.FriendsMessage
import com.orbital.snus.data.UserData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.messages_friends_list_recycler.view.*
import kotlinx.android.synthetic.main.messages_messaging_to_recycler.view.*
import java.text.SimpleDateFormat
import java.util.ArrayList

class MessagesMainPageAdapter(val users: List<UserData>, val viewModel: MessagesMainPageViewModel) :
    RecyclerView.Adapter<MessagesMainPageAdapter.UserViewHolder>() {
    val db = FirebaseFirestore.getInstance()



    class UserViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessagesMainPageAdapter.UserViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.messages_friends_list_recycler, parent, false)
        return UserViewHolder(textView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: MessagesMainPageAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.textView.message_friends_recycler_name.text = user.fullname
        viewModel.loadLatestMessage(user, holder)





        if (user.picUri != null) {
            Picasso.get().load(user.picUri).into(holder.textView.message_friends_recycler_photo)
        }
        holder.textView.setOnClickListener(onClickListener(position));
    }

    private fun onClickListener(position: Int): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("userdata", users[position])
            it.findNavController()
                .navigate(R.id.action_messagesMainPageFragment_to_messagingFragment, bundle)
        }
    }

}