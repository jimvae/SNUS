package com.orbital.snus.modules.Forum.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumQuestionBinding
import com.orbital.snus.modules.ModulesActivity
import kotlinx.android.synthetic.main.module_forum_question.*

class QuestionFragment : Fragment() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ModuleForumQuestionBinding
    private lateinit var post: ForumPost

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

        binding.buttonEdit.setOnClickListener {
            // Edit the page
        }

        binding.buttonDelete.setOnClickListener {
            // delete post
        }

        binding.buttonAnswer.setOnClickListener {
            // go to the answers page
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ModulesActivity).showNavBar()
    }

    fun initialiseView() {
        // if the user is not the poster, cannot delete or edit
        if (firebaseAuth.currentUser!!.uid != post.userid) {
            userPrivilege(false)
        }
        binding.textTitle.text = post.title
        binding.textQuestion.text = post.question
    }

    // user can see and interact with edit and delete buttons
    fun userPrivilege(boolean: Boolean) {
        binding.buttonDelete.isVisible = boolean
        binding.buttonDelete.isEnabled = boolean

        binding.buttonEdit.isVisible = boolean
        binding.buttonEdit.isEnabled = boolean
    }
}