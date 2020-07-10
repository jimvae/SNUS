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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineBinding
import com.orbital.snus.modules.Forum.Posts.Answers.AnswersAdapter
import com.orbital.snus.modules.Forum.Posts.PostViewAdapter
import com.orbital.snus.modules.Forum.Posts.PostViewModel
import com.orbital.snus.modules.Forum.Posts.PostViewModelFactory
import kotlinx.android.synthetic.main.profile_main_links_dialog.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    var _userDataObserver = MutableLiveData<Boolean>()
    var _setupObserver = MutableLiveData<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<ProfileMainTimelineBinding>(inflater,
            R.layout.profile_main_timeline, container, false)

        if (arguments != null) {
            userData = requireArguments().getParcelable<UserData>("userdata") as UserData
            _userDataObserver.value = true
        } else {
            firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    userData = it.toObject((UserData::class.java))!!
                    _userDataObserver.value = true
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
                }
        }

        _userDataObserver.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                pageSetup()
                val bundle = Bundle()
                bundle.putParcelable("userdata", userData)

                // on click listeners
                binding.mainTimelineEditSettings.setOnClickListener {
                    findNavController().navigate(R.id.action_mainTimelineFragment2_to_editProfileFragment2, bundle)
                }

                binding.mainTimelineTimeline.setOnClickListener {
                }

                binding.mainTimelineFriends.setOnClickListener {
                    findNavController().navigate(R.id.action_mainTimelineFragment2_to_mainFriendsFragment, bundle)
                }

                binding.mainTimelineAddPost.setOnClickListener {
                    findNavController().navigate(R.id.action_mainTimelineFragment2_to_addTimelinePostFragment, bundle)
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

                factory = MainTimelineViewModelFactory(userData)
                viewModel = ViewModelProvider(this@MainTimelineFragment, factory).get(MainTimelineViewModel::class.java)

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

                // NEED TO SET THE STATUS -> check if the user is in requests, requested or friends --> if not is stranger
                if (userData.userID != firebaseAuth.currentUser!!.uid) {
                    binding.textFriendStatus.text = viewModel.getUserStatus(userData.userID!!)
                }

                binding.textFriendStatus.setOnClickListener {
                    // if the text is "Add Friend" --> send friend request
                    // if the text is "Friend Request sent to @Username" --> do nothing
                    // if the text is "Friends" --> delete friend
                    // if the text is "Friend Request sent to you!" --> redirect to friend request page
                }


                // end of it
            }
        })


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null) {
            userData = requireArguments().getParcelable<UserData>("userdata") as UserData
            _setupObserver.value = true
        } else {
            firestore.collection("users").document(firebaseAuth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    userData = it.toObject((UserData::class.java))!!
                    _setupObserver.value = true
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
                }
        }

        _setupObserver.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it) {
                factory = MainTimelineViewModelFactory(userData)
                viewModel = ViewModelProvider(this, factory).get(MainTimelineViewModel::class.java)
                swipeToDelete()
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
        })

    }

    fun setUserPrivilege(boolean: Boolean) {
        binding.mainTimelineEditSettings.isVisible = boolean
        binding.mainTimelineEditSettings.isEnabled = boolean
        binding.mainTimelineAddPost.isVisible = boolean
        binding.mainTimelineAddPost.isEnabled = boolean


        binding.textFriendStatus.isVisible = !boolean
        binding.textFriendStatus.isEnabled = !boolean

    }

    fun pageSetup() {
        setUserPrivilege(userData.userID == firebaseAuth.currentUser!!.uid)
        binding.mainTimelineName.text = userData.fullname
        val facCourse : String =  userData.faculty + " (" + userData.course + ")"
        binding.mainTimelineCourse.text = facCourse
        val year: String = "Year " + userData.year.toString()
        binding.mainTimelineYear.text = year
        binding.mainTimelineBio.text = userData.bio
    }

    fun swipeToDelete(){
        if (userData.userID == firebaseAuth.currentUser!!.uid) {
            //swipe to delete if it is your comments
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val post = (viewAdapter as MainTimelineAdapter).getPost(viewHolder.adapterPosition)
                    viewModel.deletePost(post.id!!)
                    viewModel.delSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it != null) {
                            Toast.makeText(requireContext(), "Post successfully deleted", Toast.LENGTH_SHORT)
                                .show()
                            val bundle = Bundle()
                            bundle.putParcelable("userdata", userData)
                            findNavController().navigate(R.id.action_mainTimelineFragment2_self, bundle)
                            viewModel.delPostSuccessCompleted()
                        }
                    })
                    viewModel.delFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it != null) {
                            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                            viewModel.delPostFailureCompleted()
                        }
                    })


                    viewAdapter.notifyItemChanged(viewHolder.adapterPosition)
//                AnswersViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                    Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
                }
            }).attachToRecyclerView(recyclerView)
        }
    }
}