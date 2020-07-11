package com.orbital.snus.profile.MainTimeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.data.UserData

class MainFriendsViewModelFactory(val user: UserData, val currentUser: UserData) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainFriendsViewModel(user, currentUser) as T
    }
}