package com.orbital.snus.profile.MainFriends.MainTimeline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.orbital.snus.R
import com.orbital.snus.databinding.ProfileMainFriendsRequestBinding


class MainFriendsRequestFragment : Fragment() {
    private lateinit var binding: ProfileMainFriendsRequestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_main_friends_request, container, false)

        return binding.root
    }


}