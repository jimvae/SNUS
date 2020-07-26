package com.orbital.snus.modules.Review.IndividualModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.orbital.snus.R
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewIndividualModuleBinding
import com.orbital.snus.modules.Forum.IndividualModule.IndividualModuleAdapter
import com.orbital.snus.modules.Forum.IndividualModule.IndividualModuleViewModel
import com.orbital.snus.modules.Forum.IndividualModule.IndividualModuleViewModelFactory
import com.orbital.snus.modules.ModulesActivity
import java.util.ArrayList

class IndividualModuleReviewFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var factory: IndividualModuleReviewViewModelFactory
    private lateinit var viewModel: IndividualModuleReviewViewModel
    private val reviewList = ArrayList<UserReview>() // holder to store events and for RecyclerViewAdapter to observe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        val binding: ModuleReviewIndividualModuleBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_individual_module, container, false
        )

//        (requireActivity() as ModulesActivity).hideNavBar()


        val moduleName = requireArguments().get("module") as String
        binding.textModuleName.setText(moduleName + " Reviews")

        binding.reviewModuleAddReview.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("module", moduleName)
            findNavController().navigate(R.id.action_individualModuleFragment2_to_addReviewFragment, bundle)
        }

        factory = IndividualModuleReviewViewModelFactory(moduleName)
        viewModel = ViewModelProvider(this, factory).get(IndividualModuleReviewViewModel::class.java)

        viewAdapter = IndividualModuleReviewAdapter(requireArguments(), reviewList)
        viewManager = LinearLayoutManager(activity)

        recyclerView = binding.recyclerviewReviews.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.reviewList.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<UserReview>> { dbReviews ->
            reviewList.removeAll(reviewList)
            reviewList.addAll(dbReviews.sortedByDescending { it.date })
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        factory = IndividualModuleReviewViewModelFactory(requireArguments().get("module") as String)
        viewModel = ViewModelProvider(this, factory).get(IndividualModuleReviewViewModel::class.java)
        viewModel.loadReview()
        viewModel.reviewList.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<UserReview>> { reviews ->
            if (reviews.size != 0) {
//                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (activity as ModulesActivity).showNavBar()
    }

}