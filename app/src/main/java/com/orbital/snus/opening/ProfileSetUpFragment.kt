package com.orbital.snus.opening

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
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.FragmentOpeningOpeningBinding
import com.orbital.snus.databinding.FragmentOpeningProfileSetupBinding

class ProfileSetUpFragment : Fragment() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentOpeningProfileSetupBinding

    private lateinit var userData: UserData

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
        // If logged in, connect to dashboard


        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening_profile_setup, container, false
        )
        spinnerSetup()
        binding.firstLoginConfirm.setOnClickListener {
            val bio = binding.firstLoginBio.text.toString().trim()
            val name = binding.firstLoginName.text.toString().trim()
            val gitHub = binding.firstLoginGithub.text.toString().trim()
            val instagram = binding.firstLoginInstagram.text.toString().trim()
            val linkedIn = binding.firstLoginLinkedin.text.toString().trim()
            val faculty = "Computing"
//            val profilePhoto = binding.profilePhoto

            if (TextUtils.isEmpty(name)) {
                binding.firstLoginName.setError("Name is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(bio)) {
                binding.firstLoginBio.setError("Bio is required")
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

            val user = firebaseAuth.currentUser!!
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener {
                    userData = it.toObject((UserData::class.java))!!
                    userData.updateUserData(name, faculty, course, currYear.toInt(), bio, linkedIn, instagram, gitHub, false)
                    updateUser(userData)
                    startActivity(Intent(activity?.applicationContext, DashboardActivity::class.java))
                    activity?.finish()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Missing User Data", Toast.LENGTH_SHORT).show()
                }
            configurePage(true)
        }
        return binding.root
    }

    private fun spinnerSetup() {
        Toast.makeText(requireContext(), "SetUp", Toast.LENGTH_SHORT).show()
        spinnerCourse = binding.firstLoginCourseSpinner
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
                Toast.makeText(requireContext(), course, Toast.LENGTH_SHORT).show()

            }

        }

        spinnerYear = binding.firstLoginYearOfStudySpinner
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

    private fun configurePage(boolean: Boolean) {
        binding.firstLoginBio.isEnabled = boolean
        binding.firstLoginName.isEnabled = boolean
        binding.firstLoginConfirm.isEnabled = boolean
        binding.firstLoginCourseSpinner.isEnabled = boolean
        binding.firstLoginGithub.isEnabled = boolean
        binding.firstLoginInstagram.isEnabled = boolean
        binding.firstLoginLinkedin.isEnabled = boolean
        binding.profilePhoto.isEnabled = boolean
        binding.firstLoginYearOfStudySpinner.isEnabled = boolean
    }

    private fun updateUser(userData1: UserData) {
        val user = firebaseAuth.currentUser!!
        db.collection("users") // users collection
            .document(user.uid) // current userId
            .set(userData1)

    }

}