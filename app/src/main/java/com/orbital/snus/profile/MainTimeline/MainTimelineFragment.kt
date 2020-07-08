package com.orbital.snus.profile.MainTimeline

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineBinding
import kotlinx.android.synthetic.main.profile_main_links_dialog.*

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

                // page setup
                binding.mainTimelineName.text = userData.fullname
                val facCourse : String =  userData.faculty + " (" + userData.course + ")"
                binding.mainTimelineCourse.text = facCourse
                val year: String = "Year " + userData.year.toString()
                binding.mainTimelineYear.text = year
                binding.mainTimelineBio.text = userData.bio


                // on click listeners
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
                    val bundle = Bundle()
                    bundle.putParcelable("userdata", userData)
                    findNavController().navigate(R.id.action_mainTimelineFragment2_to_mainFriendsFragment, bundle)

                }

                // links: navigate to the different apps
                binding.mainTimelineLinks.setOnClickListener {
                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.profile_main_links_dialog)

                    val insta: String? = userData.insta
                    val linkedIn: String? = userData.linkedIn
                    val github: String? = userData.git

                    if (insta != null) {
                        dialog.profile_dialog_instagram.setOnClickListener {
                            val uri =
                                Uri.parse("http://instagram.com/_u/" + insta)
                            val likeIng = Intent(Intent.ACTION_VIEW, uri)

                            likeIng.setPackage("com.instagram.android")

                            try {
                                startActivity(likeIng)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://instagram.com/" + insta)
                                    )
                                )
                            }
                        }
                    }

                    if (github != null) {
                        dialog.profile_dialog_github.setOnClickListener {
                            val uri =
                                Uri.parse("http://github.com/_u/" + github)
                            val likeIng = Intent(Intent.ACTION_VIEW, uri)

                            likeIng.setPackage("com.github.android")

                            try {
                                startActivity(likeIng)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://github.com/" + github)
                                    )
                                )
                            }
                        }
                    }

                    if (linkedIn != null) {
                        dialog.profile_dialog_linkedin.setOnClickListener {
                            val uri =
                                Uri.parse("http://linkedin.com/in/" + linkedIn)
                            val likeIng = Intent(Intent.ACTION_VIEW, uri)

                            likeIng.setPackage("com.linkedin.android")

                            try {
                                startActivity(likeIng)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://linkedin.com/in/" + linkedIn)
                                    )
                                )
                            }
                        }
                    }

                    dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                    dialog.show()
                }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }


}