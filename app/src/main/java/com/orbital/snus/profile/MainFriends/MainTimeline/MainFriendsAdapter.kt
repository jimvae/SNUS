package com.orbital.snus.profile.MainTimeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.*

class MainFriendsAdapter(val users: List<UserData>) :
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
    }

}
