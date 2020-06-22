package com.orbital.snus.modules.Review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleReviewIndividualModuleBinding
import com.orbital.snus.databinding.ModuleReviewIndividualReviewBinding

class IndividualReviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleReviewIndividualReviewBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_individual_review, container, false
        )

        return binding.root
    }
}