package com.orbital.snus.modules.Forum.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumPostsUnresolvedBinding
import java.util.ArrayList

class PostsUnresolvedFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var factory: PostViewModelFactory
    private lateinit var viewModel: PostViewModel
    private val posts = ArrayList<ForumPost>() // holder to store events and for RecyclerViewAdapter to observe

    // individual forum post pages
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleForumPostsUnresolvedBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_posts_unresolved, container, false
        )

        val moduleName = (requireArguments().get("module") as String)
        val subForum = (requireArguments().get("subForum") as String)

        binding.moduleForumPostsTitles.setText("$moduleName $subForum")

        factory = PostViewModelFactory(moduleName, subForum)
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

        viewManager = LinearLayoutManager(activity)
        viewAdapter = PostViewAdapter(requireArguments(), posts)

        recyclerView = binding.forumPostsRecyclerview.apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        viewModel.posts.observe(viewLifecycleOwner, Observer<List<ForumPost>> { forumPosts ->
            posts.removeAll(posts)
            posts.addAll(forumPosts)
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        binding.buttonAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_postsUnresolvedFragment_to_askQuestionFragment, requireArguments())
        }

        binding.moduleForumPostsUnresolved.setOnClickListener {
            findNavController().navigate(R.id.action_postsUnresolvedFragment_to_postsFragment, requireArguments())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        factory = PostViewModelFactory((requireArguments().get("module") as String), (requireArguments().get("subForum") as String))
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)
        // On start of activity, we load the user data to be display on dashboard later
        viewModel.loadUnresolvedPosts()
        viewModel.posts.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<ForumPost>> { posts ->
            if (posts.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }

}