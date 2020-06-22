package com.orbital.snus.modules.Review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewDataViewModelFactory(val module: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReviewDataViewModel(module) as T
    }
}