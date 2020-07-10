package com.orbital.snus.profile.MainTimeline

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineBinding
import com.orbital.snus.modules.Forum.Posts.PostViewAdapter
import com.orbital.snus.modules.Forum.Posts.PostViewModel
import com.orbital.snus.modules.Forum.Posts.PostViewModelFactory
import kotlinx.android.synthetic.main.profile_main_links_dialog.*
import java.util.ArrayList
import java.util.Observer

class MainTimelineFragment : Fragment() {
    val firestore = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ProfileMainTimelineBinding
    private lateinit var userData: UserData

    private lateinit var factory: MainTimelineViewModelFactory
    private lateinit var viewModel: MainTimelineViewModel
    private val posts = ArrayList<TimeLinePost>()
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<ProfileMainTimelineBinding>(inflater,
            R.layout.profile_main_timeline, container, false)

        val bundle = Bundle()


        firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                userData = it.toObject((UserData::class.java))!!
                bundle.putParcelable("userdata", userData)


                // page setup
                if (!userData.userID.equals(firebaseAuth.currentUser!!.uid)) {
                    setUserPrivilege(false)
                }
                binding.mainTimelineName.text = userData.fullname
                val facCourse : String =  userData.faculty + " (" + userData.course + ")"
                binding.mainTimelineCourse.text = facCourse
                val year: String = "Year " + userData.year.toString()
                binding.mainTimelineYear.text = year
                binding.mainTimelineBio.text = userData.bio


                // on click listeners
                binding.mainTimelineEditSettings.setOnClickListener {

                    findNavController().navigate(R.id.action_mainTimelineFragment2_to_editProfileFragment2, bundle)
                }

                binding.mainTimelineTimeline.setOnClickListener {
                    // recycler view loads timeline?
                }

                val bundle = Bundle()
                bundle.putParcelable("userdata", userData)

                binding.mainTimelineFriends.setOnClickListener {
                    // recycler view loads friends?
                    // friend requests?

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

        factory = MainTimelineViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(MainTimelineViewModel::class.java)

        viewManager = LinearLayoutManager(activity)
        viewAdapter = MainTimelineAdapter(posts)

        recyclerView = binding.recyclerviewTimeline.apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        viewModel.timelinePosts.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<TimeLinePost>> { timelinePost ->
            posts.removeAll(posts)
            posts.addAll(timelinePost)
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        binding.mainTimelineAddPost.setOnClickListener {

            findNavController().navigate(R.id.action_mainTimelineFragment2_to_addTimelinePostFragment, bundle)
        }



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        factory = MainTimelineViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(MainTimelineViewModel::class.java)
        // On start of activity, we load the user data to be display on dashboard later
        viewModel.loadPosts()
        viewModel.timelinePosts.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<TimeLinePost>> { posts ->
            if (posts.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setUserPrivilege(boolean: Boolean) {
        binding.mainTimelineEditSettings.isVisible = boolean
        binding.mainTimelineEditSettings.isEnabled = boolean

        binding.mainTimelineAddPost.isVisible = boolean
        binding.mainTimelineAddPost.isEnabled = boolean


    }


}