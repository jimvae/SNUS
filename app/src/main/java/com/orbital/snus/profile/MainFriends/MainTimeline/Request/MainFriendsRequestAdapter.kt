package com.orbital.snus.profile.MainFriends.MainTimeline.Request

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.Friends
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserFriendRequest
import com.orbital.snus.profile.MainTimeline.MainFriendsViewModel
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.profile_friends_recycler_course
import kotlinx.android.synthetic.main.profile_main_friends_recycler.view.profile_friends_recycler_name
import kotlinx.android.synthetic.main.profile_main_friends_request_recycler.view.*
import kotlinx.coroutines.processNextEventInCurrentThread

class MainFriendsRequestAdapter (val users: List<UserFriendRequest>, val viewModel: MainFriendsViewModel, val currentUserData: UserData) :
    RecyclerView.Adapter<MainFriendsRequestAdapter.UserViewHolder>() {

    class UserViewHolder (val textView: View) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_main_friends_request_recycler, parent, false)
        return UserViewHolder(
            textView
        )
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userRequest = users[position]
        holder.textView.profile_friends_request_recycler_name.text = userRequest.fromName
        holder.textView.profile_friends_request_recycler_course.text = userRequest.fromCourse
//        holder.textView.setOnClickListener(onClickListenerSeeProfile(position));

        holder.textView.button_accept.setOnClickListener(
            onClickListenerAccept(userRequest))
        holder.textView.button_decline.setOnClickListener(
            onClickListenDecline(userRequest))
    }

//    private fun onClickListenerSeeProfile(position: Int): View.OnClickListener? {
//        return View.OnClickListener {
//            val bundle = Bundle()
//            bundle.putParcelable("userdata", users[position])
//            bundle.putParcelable("currentUserData", currentUserData )
//            it.findNavController().navigate(R.id.action_mainFriendsRequestFragment_to_mainFriendsFragment, bundle)
//        }
//    }

    private fun onClickListenerAccept(request: UserFriendRequest): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("userdata", currentUserData)
            bundle.putParcelable("currentUserData", currentUserData )
            val otherUserInfo = Friends(null, request.fromID, request.fromName, request.fromCourse)
            val currentUserInfo= Friends(null, currentUserData.userID, currentUserData.fullname, currentUserData.course)
            viewModel.acceptRequest(currentUserInfo, otherUserInfo)
            viewModel.declineRequest(request.id!!)



            it.findNavController().navigate(R.id.action_mainFriendsRequestFragment_to_mainFriendsFragment,bundle)
        }
    }

    private fun onClickListenDecline(request: UserFriendRequest): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("userdata", currentUserData)
            bundle.putParcelable("currentUserData", currentUserData )
            viewModel.declineRequest(request.id!!)

            it.findNavController().navigate(R.id.action_mainFriendsRequestFragment_to_mainFriendsFragment, bundle)
        }
    }

}
