package com.orbital.snus.profile.MainTimeline

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineAddPostBinding
import com.orbital.snus.databinding.ProfileMainTimelineEditBinding
import com.orbital.snus.profile.ProfileActivity
import java.util.*

class AddTimelinePostFragment : Fragment() {
    private lateinit var binding: ProfileMainTimelineAddPostBinding
    val db = FirebaseFirestore.getInstance()
    private lateinit var viewModel: MainTimelineViewModel
    private lateinit var factory: MainTimelineViewModelFactory

    val categories = arrayOf("", "Camps", "Medium", "High")
    private lateinit var categorySpinner: Spinner
    private lateinit var category: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        factory = MainTimelineViewModelFactory(requireArguments().get("userdata") as UserData)
        viewModel = ViewModelProvider(this, factory).get(MainTimelineViewModel::class.java)


        (activity as ProfileActivity).hideNavBar()
        var userData : UserData = requireArguments().getParcelable<UserData>("userdata") as UserData

        binding = DataBindingUtil.inflate<ProfileMainTimelineAddPostBinding>(
            inflater, R.layout.profile_main_timeline_add_post, container, false
        )
        spinnerSetup()


        binding.mainTimelineAddPostConfirm.setOnClickListener {
            val title = binding.mainTimelineAddPostTitle.text.toString().trim()
//            val details = binding.mainTimelineAddExtraDetails.text.toString().trim()

            if (TextUtils.isEmpty(title)) {
                binding.mainTimelineAddPostTitle.setError("Missing Title")
                return@setOnClickListener
            }

            if (category == "") {
                Toast.makeText(requireContext(), "Please enter more details", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
//            if (TextUtils.isEmpty(details)) {
//                binding.mainTimelineAddPostTitle.setError("Missing Details")
//                return@setOnClickListener
//            }

            configurePage(false)

            val post = TimeLinePost(null, title, Calendar.getInstance().time, false, category, null)
            viewModel.addTimeline(post)
            viewModel.addSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully added", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_addTimelinePostFragment_to_mainTimelineFragment2, requireArguments())
                    viewModel.addPostSuccessCompleted()
                }
            })
            viewModel.addFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.addPostFailureCompleted()
                }
            })

        }
        return binding.root
    }

    private fun configurePage(boolean: Boolean) {
        binding.mainTimelineAddPostConfirm.isEnabled = boolean
        binding.mainTimelineAddExtraDetails.isEnabled = boolean
        binding.mainTimelineAddPostTitle.isEnabled = boolean
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ProfileActivity).showNavBar()
    }

    fun spinnerSetup() {
        categorySpinner = binding.mainTimelineAddExtraDetails
        categorySpinner.adapter =
            ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, categories)

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                category = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                category = categories.get(position)
            }
        }
    }
}