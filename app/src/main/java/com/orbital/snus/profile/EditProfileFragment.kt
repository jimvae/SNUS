package com.orbital.snus.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
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
        binding = DataBindingUtil.inflate<ProfileMainTimelineEditBinding>(
            inflater, R.layout.profile_main_timeline_edit, container, false)
        spinnerSetup()

        var userData : UserData = requireArguments().getParcelable<UserData>("userdata") as UserData

        binding.editProfileName.setText(userData.fullname)
        binding.editProfileBio.setText(userData.bio)
        binding.editProfileInstagram.setText(userData.insta)
        binding.editProfileGithub.setText(userData.git)
        binding.editProfileLinkedin.setText(userData.linkedIn)
        binding.editProfileCourseSpinner.setSelection(courses.indexOf(userData.course))
        binding.editProfileYearOfStudySpinner.setSelection(year.indexOf(userData.year.toString()))

        return binding.root
    }

    private fun updateUser(userData1: UserData) {
        val user = firebaseAuth.currentUser!!
        db.collection("users") // users collection
            .document(user.uid) // current userId
            .set(userData1)

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
}