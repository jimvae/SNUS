package com.orbital.snus.profile.MainTimeline

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineBinding

class MainTimelineFragment : Fragment() {
    val firestore = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<ProfileMainTimelineBinding>(inflater,
            R.layout.profile_main_timeline, container, false)

        firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                userData = it.toObject((UserData::class.java))!!

                binding.mainTimelineName.text = userData.fullname
                val facCourse : String =  userData.faculty + " (" + userData.course + ")"
                binding.mainTimelineCourse.text = facCourse
                val year: String = "Year " + userData.year.toString()
                binding.mainTimelineYear.text = year
                binding.mainTimelineBio.text = userData.bio

                binding.mainTimelineEditSettings.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("userdata", userData)
                    findNavController().navigate(R.id.action_mainTimelineFragment2_to_editProfileFragment2, bundle)
                }

                binding.mainTimelineTimeline.setOnClickListener {
                    // recycler view loads timeline?
                }

                binding.mainTimelineFriends.setOnClickListener {
                    // recycler view loads friends?
                    // friend requests?
                }

                // links: navigate to the different apps
                binding.mainTimelineInsta.setOnClickListener {

                }

                binding.mainTimelineLinkedin.setOnClickListener {

                }

                binding.mainTimelineGithub.setOnClickListener {

                }


            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }


}