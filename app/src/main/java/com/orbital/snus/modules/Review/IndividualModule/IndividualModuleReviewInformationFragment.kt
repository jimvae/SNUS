package com.orbital.snus.modules.Review.IndividualModule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleReviewIndividualReviewInformationBinding

class IndividualModuleReviewInformationFragment : Fragment() {

    private lateinit var binding: ModuleReviewIndividualReviewInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil
            .inflate(layoutInflater, R.layout.module_review_individual_review_information, container, false)

        // figure out how to access NUSMODS API to extract module information
        // maybe if it works can consider how to include other information
        // like if can SU, prerequisites, content (lab, lect etc)
        // or just load the nusmods page in the app? xd

        return binding.root
    }
}