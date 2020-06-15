package com.orbital.snus.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.dashboard.Today.TodayViewModel

@Suppress("UNCHECKED_CAST")
class DashboardViewModelFactory() : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardViewModel() as T
    }
}