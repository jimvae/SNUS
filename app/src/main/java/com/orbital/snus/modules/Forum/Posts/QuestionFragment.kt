package com.orbital.snus.modules.Forum.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumQuestionBinding
import com.orbital.snus.modules.ModulesActivity
import kotlinx.android.synthetic.main.module_forum_question.*
import kotlin.properties.Delegates

class QuestionFragment : Fragment() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ModuleForumQuestionBinding
    private lateinit var post: ForumPost

    private lateinit var viewModel: PostViewModel
    private lateinit var factory: PostViewModelFactory

    var userPrivilege: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_question, container, false)

        post = (requireArguments().get("post") as ForumPost)
        initialiseView()

        factory = PostViewModelFactory(requireArguments().get("module") as String, requireArguments().get("subForum") as String)
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

        binding.buttonAnswer.setOnClickListener {
            // go to the answers page
        }

        binding.buttonEdit.setOnClickListener {
            // Edit the page
        }

        binding.buttonDelete.setOnClickListener {
            // delete post
            configurePage(false)
            viewModel.deletePost(post.id!!)
            viewModel.delSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully deleted", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_questionFragment_to_postsFragment, requireArguments())
                    viewModel.delEventSuccessCompleted()
                }
            })
            viewModel.delFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.delEventFailureCompleted()
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ModulesActivity).showNavBar()
    }

    fun initialiseView() {
        // if the user is not the poster, cannot delete or edit
        setUserPrivilege(firebaseAuth.currentUser!!.uid == post.userid)
        binding.textTitle.text = post.title
        binding.textQuestion.text = post.question
    }

    // user can see and interact with edit and delete buttons
    fun setUserPrivilege(boolean: Boolean) {
        userPrivilege = boolean
        binding.buttonDelete.isVisible = boolean
        binding.buttonDelete.isEnabled = boolean

        binding.buttonEdit.isVisible = boolean
        binding.buttonEdit.isEnabled = boolean
    }

    fun configurePage(boolean: Boolean) {
        binding.buttonAnswer.isEnabled = boolean
        if (userPrivilege == true) {
            binding.buttonEdit.isEnabled = boolean
            binding.buttonDelete.isEnabled = boolean
        }
    }
}