package com.orbital.snus.modules.Forum.Posts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumAskquestionBinding
import com.orbital.snus.databinding.ModuleForumPostsBinding
import com.orbital.snus.modules.ModulesActivity
import java.util.*

class AskQuestionFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var factory: PostViewModelFactory
    private lateinit var viewModel: PostViewModel

    private lateinit var binding: ModuleForumAskquestionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_askquestion, container, false)

        val moduleName = (requireArguments().get("module") as String)
        val subForum = (requireArguments().get("subForum") as String)

        factory = PostViewModelFactory(moduleName, subForum)
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

        binding.editQuestion.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        binding.buttonConfirm.setOnClickListener {
            hideKeyboard(it)
            val title = binding.editTitle.text.toString()
            val question = binding.editQuestion.text.toString()

            if (title == "") {
                Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (question == "") {
                Toast.makeText(requireContext(), "Please enter question", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            configurePage(false)

            val calendar = Calendar.getInstance()
            val post: ForumPost = ForumPost(firebaseAuth.currentUser!!.uid, null,  title,
                calendar.time, false, question)
            viewModel.addPost(post)

            viewModel.addSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully added", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_askQuestionFragment_to_postsFragment, requireArguments())
                    viewModel.addPostSuccessCompleted()
                }
            })
            viewModel.addFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.addPostFailureCompleted()
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ModulesActivity).showNavBar()
    }

    fun configurePage(boolean: Boolean) {
        binding.editTitle.isEnabled = boolean
        binding.editQuestion.isEnabled = boolean
        binding.buttonConfirm.isEnabled = boolean
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_askQuestionFragment_to_postsFragment, requireArguments())
            }
        })
    }
}