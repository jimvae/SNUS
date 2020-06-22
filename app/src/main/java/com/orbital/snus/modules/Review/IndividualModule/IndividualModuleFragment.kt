package com.orbital.snus.modules.Review.IndividualModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleReviewIndividualModuleBinding
import com.orbital.snus.databinding.ModuleReviewMainPageBinding

class IndividualModuleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleReviewIndividualModuleBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_individual_module, container, false
        )

        binding.button2.setOnClickListener {
            view : View -> view.findNavController().navigate(R.id.action_individualModuleFragment2_to_individualReviewFragment)
        }


        return binding.root
    }
}