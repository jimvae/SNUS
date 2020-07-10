package com.orbital.snus.profile.MainTimeline

import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.modules.Review.ReviewDataViewModel

class MainTimelineViewModelFactory(val user: com.orbital.snus.data.UserData) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainTimelineViewModel(user) as T
    }
}