package com.orbital.snus.profile.MainFriends.MainTimeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.profile.MainTimeline.MainFriendsAdapter
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModel
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModelFactory
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.*
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.profile_friends_recycler_course
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.profile_friends_recycler_name
import kotlinx.android.synthetic.main.profile_main_friends_request_recycler.view.*

class MainFriendsRequestAdapter (val users: List<UserData>, val viewModel: MainFriendsViewModel) :
    RecyclerView.Adapter<MainFriendsRequestAdapter.UserViewHolder>() {

    class UserViewHolder (val textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainFriendsRequestAdapter.UserViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_main_friends_request_recycler, parent, false)
        return UserViewHolder(textView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: MainFriendsRequestAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.textView.profile_friends_recycler_name.text = user.fullname
        holder.textView.profile_friends_recycler_course.text = user.course
        holder.textView.setOnClickListener(onClickListener(position));

        holder.textView.button_accept.setOnClickListener {

        }
        holder.textView.button_decline.setOnClickListener {

        }
    }

    private fun onClickListener(position: Int): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("userdata", users[position])
            it.findNavController().navigate(R.id.action_mainFriendsRequestFragment_to_mainFriendsFragment, bundle)
        }
    }

}
