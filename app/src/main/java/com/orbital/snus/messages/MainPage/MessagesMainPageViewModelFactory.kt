package com.orbital.snus.messages.MainPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.messages.NewMessage.NewMessageViewModel

class MessagesMainPageViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessagesMainPageViewModel() as T
    }
}