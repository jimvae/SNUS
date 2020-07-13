package com.orbital.snus.profile.MainTimeline

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.orbital.snus.R
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserFriendRequest
import com.orbital.snus.databinding.ProfileMainFriendsBinding
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_main_links_dialog.*

class MainFriendsFragment : Fragment() {

    // when page loads
    // if it is user == firebase user -> can edit setting, can search friends, can see friends.
    // if not user, can only view profile and timeline

    private lateinit var binding: ProfileMainFriendsBinding
    private lateinit var viewModel: MainFriendsViewModel
    private lateinit var factory: MainFriendsViewModelFactory

    private val friends = ArrayList<UserData>()

    // the recyclerview here should display the user's friends
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView

    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<ProfileMainFriendsBinding>(
            inflater, R.layout.profile_main_friends, container, false)


        userData = requireArguments().getParcelable<UserData>("userdata") as UserData
        val currentUserData = requireArguments().getParcelable<UserData>("currentUserData") as UserData

        setUserPrivilege(userData.userID == firebaseAuth.currentUser!!.uid)
        factory = MainFriendsViewModelFactory(userData,currentUserData)
        viewModel = ViewModelProvider(this, factory).get(MainFriendsViewModel::class.java)

        viewManager = LinearLayoutManager(activity)
        viewAdapter = MainFriendsAdapter(currentUserData, friends)
        recyclerView = binding.recyclerviewProfile.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.friends.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            users ->
            friends.removeAll(friends)
            friends.addAll(users)
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        // page setup
        binding.mainProfileName.text = userData.fullname
        val facCourse : String =  userData.faculty + " (" + userData.course + ")"
        binding.mainProfileCourse.text = facCourse
        val year: String = "Year " + userData.year.toString()
        binding.mainProfileYear.text = year
        binding.mainProfileBio.text = userData.bio
        if (userData.picUri != null) {
            Picasso.get().load(userData.picUri).into(binding.mainFriendPhotoView)
        }

        val bundle = Bundle()
        bundle.putParcelable("userdata", userData)
        bundle.putParcelable( "currentUserData", currentUserData)


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

        binding.buttonRequests.setOnClickListener {
            findNavController().navigate(R.id.action_mainFriendsFragment_to_mainFriendsRequestFragment, bundle)
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

        binding.buttonSearch.setOnClickListener {
            hideKeyboard(it)

            val text: String = binding.editTextSearch.text.toString()
            if (text == "") {
                binding.editTextSearch.setError("User cannot be empty!")
                return@setOnClickListener
            }

            // pass the text into the search fragment.
            // let the search fragment handle the search of users
            val searchBundle = Bundle()
            searchBundle.putString("search", text)
            searchBundle.putParcelable("userdata", userData)
            searchBundle.putParcelable("currentUserData", currentUserData)

            findNavController().navigate(R.id.action_mainFriendsFragment_to_mainFriendsSearchFragment, searchBundle)
        }

        // NEED TO SET THE STATUS -> check if the user is in requests, requested or friends --> if not is stranger
        if (userData.userID != firebaseAuth.currentUser!!.uid) {
            viewModel.getUserStatus(userData.userID!!)
            viewModel.userStatus.observe(viewLifecycleOwner, Observer {
                binding.textFriendStatus.text = it
            })
        }

        binding.textFriendStatus.setOnClickListener {
            // if the text is "Add Friend" --> send friend request
            // if the text is "Friend Request sent to @Username" --> do nothing
            // if the text is "Friends" --> delete friend
            // if the text is "Friend Request sent to you!" --> redirect to friend request page
            when (binding.textFriendStatus.text) {
                "Add Friend" -> {
                    viewModel.sendRequest(UserFriendRequest(null, firebaseAuth.currentUser!!.uid, userData.userID,
                        currentUserData.fullname, currentUserData.course, currentUserData.picUri))
                    viewModel.sendSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it == true) {
                            binding.textFriendStatus.text = "Friend Request Sent!"
                            viewModel.sendSuccessCompleted()
                        }
                    })
                    viewModel.sendFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it != null) {
                            Toast.makeText(requireContext(), "Failed: " + it.message, Toast.LENGTH_SHORT).show()
                        }
                        viewModel.sendFailureCompleted()
                    })
                }
                "Friends" -> {
                    // add delete friend?
                    return@setOnClickListener
                }
                "Friend Request Sent!" -> {
                    // add cancel friend request?
                    // or do nothing
                    return@setOnClickListener
                }
                "Friend Request Sent to You!" -> {
                    // navigate to friend requests
                    findNavController().navigate(R.id.action_mainFriendsFragment_to_mainFriendsRequestFragment)
                    return@setOnClickListener
                }
            }
        }

        return binding.root
    }

    fun configurePage(boolean: Boolean) {
        binding.mainProfileEditSettings.isEnabled = boolean
        binding.mainFriendsLinks.isEnabled = boolean
        binding.mainProfileTimeline.isEnabled = boolean
        binding.mainProfileFriends.isEnabled = boolean
        binding.editTextSearch.isEnabled = boolean
        binding.buttonSearch.isEnabled = boolean
        binding.recyclerviewProfile.isEnabled = boolean
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setUserPrivilege(boolean: Boolean) {
        binding.mainProfileEditSettings.isVisible = boolean
        binding.mainProfileEditSettings.isEnabled = boolean

        binding.textFriendStatus.isVisible = !boolean
        binding.textFriendStatus.isEnabled = !boolean

        binding.buttonRequests.isVisible = boolean
        binding.buttonRequests.isEnabled = boolean
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val currentUserData = requireArguments().getParcelable<UserData>("currentUserData") as UserData

        factory = MainFriendsViewModelFactory(userData,currentUserData)
        viewModel = ViewModelProvider(this, factory).get(MainFriendsViewModel::class.java)
        viewModel.loadUsers()
        viewModel.users.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<UserData>> { users ->
            if (users.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
