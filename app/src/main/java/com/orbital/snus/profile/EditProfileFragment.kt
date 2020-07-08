package com.orbital.snus.profile

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineEditBinding

class EditProfileFragment : Fragment() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ProfileMainTimelineEditBinding

    val courses = arrayOf("", "Computer Science", "Business Analytics", "Computer Engineering", "Information Systems", "Information Security")
    private lateinit var spinnerCourse: Spinner
    private lateinit var course: String

    val year = arrayOf("", "1", "2", "3", "4", "5")
    private lateinit var spinnerYear: Spinner
    private lateinit var currYear: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as ProfileActivity).hideNavBar()

        binding = DataBindingUtil.inflate<ProfileMainTimelineEditBinding>(
            inflater, R.layout.profile_main_timeline_edit, container, false)
        spinnerSetup()

        var userData : UserData = requireArguments().getParcelable<UserData>("userdata") as UserData

        // setup page
        binding.editProfileName.setText(userData.fullname)
        binding.editProfileBio.setText(userData.bio)
        binding.editProfileInstagram.setText(userData.insta)
        binding.editProfileGithub.setText(userData.git)
        binding.editProfileLinkedin.setText(userData.linkedIn)
        binding.editProfileCourseSpinner.setSelection(courses.indexOf(userData.course))
        binding.editProfileYearOfStudySpinner.setSelection(year.indexOf(userData.year.toString()))

        course = userData.course!!
        currYear = userData.year.toString()

        binding.editProfileConfirm.setOnClickListener {
            val bio = binding.editProfileBio.text.toString().trim()
            val name = binding.editProfileName.text.toString().trim()
            val gitHub = binding.editProfileGithub.text.toString().trim()
            val instagram = binding.editProfileInstagram.text.toString().trim()
            val linkedIn = binding.editProfileLinkedin.text.toString().trim()
            val faculty = "Computing"

            if (TextUtils.isEmpty(name)) {
                binding.editProfileName.setError("Name is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(bio)) {
                binding.editProfileBio.setError("Bio is required")
                return@setOnClickListener
            }

            if (course == "") {
                Toast.makeText(requireContext(), "Please select your course", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (currYear == "") {
                Toast.makeText(requireContext(), "Please select your year", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            configurePage(false)

            userData.updateUserData(name, faculty, course, currYear.toInt(), bio, linkedIn, instagram, gitHub, false)
            db.collection("users") // users collection
                .document(userData.userID!!) // current userId
                .set(userData)
                .addOnSuccessListener {
                    findNavController().navigate(R.id.action_editProfileFragment2_to_mainTimelineFragment2)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Unable to update: " + it.message, Toast.LENGTH_SHORT).show()
                }
            configurePage(true)
        }
        return binding.root
    }


    private fun configurePage(boolean: Boolean) {
        binding.editProfileBio.isEnabled = boolean
        binding.editProfileName.isEnabled = boolean
        binding.editProfileConfirm.isEnabled = boolean
        binding.editProfileCourseSpinner.isEnabled = boolean
        binding.editProfileGithub.isEnabled = boolean
        binding.editProfileInstagram.isEnabled = boolean
        binding.editProfileLinkedin.isEnabled = boolean
        binding.profilePhoto.isEnabled = boolean
        binding.editProfileYearOfStudySpinner.isEnabled = boolean
    }

    private fun spinnerSetup() {
        Toast.makeText(requireContext(), "SetUp", Toast.LENGTH_SHORT).show()
        spinnerCourse = binding.editProfileCourseSpinner
        spinnerCourse.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, courses)

        spinnerCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                course = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                course = courses.get(position)

            }

        }

        spinnerYear = binding.editProfileYearOfStudySpinner
        spinnerYear.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, year)

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                currYear = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currYear = year.get(position)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ProfileActivity).showNavBar()
    }
}