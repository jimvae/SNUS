package com.orbital.snus.modules.Forum.Posts.Answers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.modules.Forum.Posts.PostViewModel

class AnswersViewModelFactory(val module: String, val subForum: String, val question: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AnswersViewModel(module, subForum, question) as T
    }
}