package com.orbital.snus.modules.Review.IndividualModule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orbital.snus.modules.Forum.IndividualModule.IndividualModuleViewModel

class IndividualModuleReviewViewModelFactory(val moduleName: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IndividualModuleReviewViewModel(moduleName) as T
    }
}