package com.orbital.snus.messages.NewMessage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.messages.Messaging.MessagingViewModel

class NewMessageViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewMessageViewModel() as T
    }
}