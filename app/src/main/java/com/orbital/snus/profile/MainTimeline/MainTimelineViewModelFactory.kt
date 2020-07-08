package com.orbital.snus.profile.MainTimeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.modules.Review.ReviewDataViewModel

class MainTimelineViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainTimelineViewModel() as T
    }
}