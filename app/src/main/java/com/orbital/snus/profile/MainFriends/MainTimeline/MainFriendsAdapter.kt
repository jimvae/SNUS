package com.orbital.snus.profile.MainTimeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.*
import kotlinx.android.synthetic.main.profile_main_friends_request_recycler.view.*

class MainFriendsAdapter(val currentUserData: UserData, val users: List<UserData>) :
    RecyclerView.Adapter<MainFriendsAdapter.UserViewHolder>() {

    class UserViewHolder (val textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainFriendsAdapter.UserViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_main_friends_recycler, parent, false)
        return UserViewHolder(textView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: MainFriendsAdapter.UserViewHolder, position: Int) {
        val user = users[position]
        holder.textView.profile_friends_recycler_name.text = user.fullname
        holder.textView.profile_friends_recycler_course.text = user.course
        if (user.picUri != null) {
            Picasso.get().load(user.picUri).into(holder.textView.profile_friends_recycler_photo)
        }
        holder.textView.setOnClickListener(onClickListener(position));
    }

    private fun onClickListener(position: Int): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("userdata", users[position])
            bundle.putParcelable("currentUserData", currentUserData)
            it.findNavController().navigate(R.id.action_mainFriendsFragment_to_mainTimelineFragment2, bundle)
        }
    }

}
