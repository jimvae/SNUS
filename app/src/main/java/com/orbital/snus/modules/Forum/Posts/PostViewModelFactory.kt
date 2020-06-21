package com.orbital.snus.modules.Forum.Posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PostViewModelFactory(val module: String, val subForum: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostViewModel(module, subForum) as T
    }
}