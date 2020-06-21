package com.orbital.snus.modules.Forum.IndividualModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ModuleViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ModuleViewModel() as T
    }
}