package com.orbital.snus.dashboard.Upcoming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class UpcomingViewModelFactory  : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UpcomingViewModel() as T
    }
}