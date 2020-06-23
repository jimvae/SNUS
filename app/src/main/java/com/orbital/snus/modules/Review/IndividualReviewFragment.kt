package com.orbital.snus.modules.Review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.orbital.snus.R
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewIndividualModuleBinding
import com.orbital.snus.databinding.ModuleReviewIndividualReviewBinding
import java.text.SimpleDateFormat

class IndividualReviewFragment : Fragment() {
    private lateinit var binding: ModuleReviewIndividualReviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_individual_review, container, false
        )

        val review = requireArguments().get("review") as UserReview

        initialiseViews(review)

        return binding.root
    }

    fun initialiseViews(review: UserReview) {
        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("dd MMM yy")

        binding.individualReviewTitle.text = review.title
        binding.individualReviewActual.text = review.actualGrade
        binding.individualReviewCommitment.text = review.commitment
        binding.individualReviewDate.text = dateFormatter1.format(review.date!!).toPattern().toString()
        binding.individualReviewExpected.text = review.expectedGrade
        binding.individualReviewProf.text = review.prof
        binding.individualReviewDescription.text = review.description
        binding.individualReviewWorkload.text = review.workload
        binding.individualReviewRatings.text = review.rating.toString() + " out of 5"


    }
}