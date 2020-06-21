package com.orbital.snus.modules.Forum.IndividualModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class IndividualModuleViewModelFactory(val moduleName: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IndividualModuleViewModel(moduleName) as T
    }
}