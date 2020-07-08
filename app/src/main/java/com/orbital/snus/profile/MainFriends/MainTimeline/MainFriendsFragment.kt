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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.orbital.snus.R
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainFriendsBinding
import kotlinx.android.synthetic.main.profile_main_links_dialog.*

class MainFriendsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<ProfileMainFriendsBinding>(
            inflater, R.layout.profile_main_friends, container, false)

        var userData : UserData = requireArguments().getParcelable<UserData>("userdata") as UserData

        // page setup
        binding.mainProfileName.text = userData.fullname
        val facCourse : String =  userData.faculty + " (" + userData.course + ")"
        binding.mainProfileCourse.text = facCourse
        val year: String = "Year " + userData.year.toString()
        binding.mainProfileYear.text = year
        binding.mainProfileBio.text = userData.bio

        val bundle = Bundle()
        bundle.putParcelable("userdata", userData)

        // on click listeners
        binding.mainProfileEditSettings.setOnClickListener {
            findNavController().navigate(R.id.action_mainFriendsFragment_to_editProfileFragment2, bundle)
        }

        binding.mainProfileTimeline.setOnClickListener {
            findNavController().navigate(R.id.action_mainFriendsFragment_to_mainTimelineFragment2, bundle)
        }

        binding.mainProfileFriends.setOnClickListener {
            // does nothing
        }

        // links: navigate to the different apps
        binding.mainFriendsLinks.setOnClickListener {
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
        return binding.root
    }
}
