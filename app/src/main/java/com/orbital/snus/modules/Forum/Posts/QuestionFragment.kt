package com.orbital.snus.modules.Forum.Posts

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumQuestionBinding
import com.orbital.snus.modules.ModulesActivity
import kotlinx.android.synthetic.main.module_forum_individual_module.*
import kotlinx.android.synthetic.main.module_forum_question.*
import kotlinx.android.synthetic.main.module_forum_question_dialog_edit.*
import kotlin.properties.Delegates

class QuestionFragment : Fragment() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ModuleForumQuestionBinding
    private lateinit var post: ForumPost

    private lateinit var viewModel: PostViewModel
    private lateinit var factory: PostViewModelFactory

    var userPrivilege: Boolean? = null

    private lateinit var dialog: Dialog

    var toAns: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_question, container, false)

        post = (requireArguments().get("post") as ForumPost)
        initiateViews()

        val module = requireArguments().get("module") as String
        val subForum = requireArguments().get("subForum") as String
        val question = post.id

        factory = PostViewModelFactory(module, subForum)
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

        binding.buttonAnswer.setOnClickListener {
            // go to the answers page
            toAns = true

            val bundle = Bundle()
            bundle.putString("module", module)
            bundle.putString("subForum", subForum)
            bundle.putString("question", question)

            it.findNavController().navigate(R.id.action_questionFragment_to_answersFragment, bundle)
        }

        binding.buttonEdit.setOnClickListener {
            // Edit the page
            dialog = Dialog(requireContext())
            showPopup(it)
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
                    viewModel.delPostSuccessCompleted()
                }
            })
            viewModel.delFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.delPostFailureCompleted()
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (toAns) {
            (activity as ModulesActivity).hideNavBar()
        } else {
            (activity as ModulesActivity).showNavBar()
        }
        toAns = false
    }


    fun initiateViews() {
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

    fun showPopup(v: View?) {
        dialog.setContentView(R.layout.module_forum_question_dialog_edit)
        dialog.edit_title.setText(binding.textTitle.text)
        dialog.edit_question.setText(binding.textQuestion.text)

        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.edit_close.setOnClickListener { dialog.dismiss() }
        dialog.button_confirm.setOnClickListener {
            if (dialog.edit_title.text.toString() == "" || dialog.edit_question.text.toString() == "") {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            configureDialog(false)
            configurePage(false)

            post.updatePost(dialog.edit_title.text.toString(), dialog.edit_question.text.toString())

            viewModel.updatePost(post)

            viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully updated", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updatePostSuccessCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)

                    //updates the field of events fragment
                    initiateViews()
                    dialog.dismiss()
                }
            })
            viewModel.updateFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.updatePostFailureCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)
                }
            })

        }
    }

    fun configureDialog(boolean: Boolean) {
        dialog.edit_title.isEnabled = boolean
        dialog.edit_question.isEnabled = boolean
        dialog.edit_close.isEnabled = boolean
        dialog.button_confirm.isEnabled = boolean
    }

//    private fun onClickListener(module:String, subForum:String, question: String): View.OnClickListener? {
//        return View.OnClickListener {
//            val bundle = Bundle()
//            bundle.putString("module", module)
//            bundle.putString("subForum", subForum)
//            bundle.putString("question", question)
//
//            it.findNavController().navigate(R.id.action_questionFragment_to_answersFragment, bundle)
//        }
//    }
}