package com.orbital.snus.profile.MainTimeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainFriendsViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainFriendsViewModel() as T
    }
}