package com.orbital.snus.modules.Forum.Posts.Answers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.ForumComment
import com.orbital.snus.databinding.ModuleForumAnswersBinding
import com.orbital.snus.modules.ModulesActivity
import java.util.*


class AnswersFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ModuleForumAnswersBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var factory: AnswersViewModelFactory
    private lateinit var viewModel: AnswersViewModel
    private val answers =
        ArrayList<ForumComment>() // holder to store events and for RecyclerViewAdapter to observe


    // individual forum post pages
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_answers, container, false
        )

        val moduleName = (requireArguments().get("module") as String)
        val subForum = (requireArguments().get("subForum") as String)
        val question = (requireArguments().get("question") as String)
        factory = AnswersViewModelFactory(moduleName, subForum, question)
        viewModel = ViewModelProvider(this, factory).get(AnswersViewModel::class.java)

        viewManager = LinearLayoutManager(activity)
        viewAdapter = AnswersAdapter(answers)

        recyclerView = binding.forumAnswersRecyclerView.apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        viewModel.answers.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<List<ForumComment>> { forumComments ->
                answers.removeAll(answers)
                answers.addAll(forumComments)
                recyclerView.adapter!!.notifyDataSetChanged()
            })



        binding.moduleForumAnswersSend.setOnClickListener {
            hideKeyboard(it)
            val text = binding.moduleForumAnswersMessageHere.text.toString()

            if (text == "") {
                Toast.makeText(requireContext(), "Please enter message", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            configurePage(false)

            val calendar = Calendar.getInstance()
            val answer: ForumComment = ForumComment(null,
                firebaseAuth.currentUser!!.uid, calendar.time, text)

            viewModel.addAnswers(answer)

            viewModel.addSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Comment successfully added", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(
                        R.id.action_answersFragment_self,
                        requireArguments()
                    )
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


        //swipe to delete if it is your comments
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val comment = (viewAdapter as AnswersAdapter).getComment(viewHolder.adapterPosition)
                if (comment.userID == firebaseAuth.currentUser!!.uid) {
                    viewModel.deleteComment(comment.forumID!!)
                    viewModel.delSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                        if (it != null) {
                            Toast.makeText(requireContext(), "Comment successfully deleted", Toast.LENGTH_SHORT)
                                .show()
//                            findNavController().navigate(R.id.action_answersFragment_self, requireArguments())
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
                viewAdapter.notifyItemChanged(viewHolder.adapterPosition)
//                AnswersViewModel.delete(adapter.getNoteAt(viewHolder.adapterPosition))
                Toast.makeText(requireContext(), "Note deleted", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val moduleName = (requireArguments().get("module") as String)
        val subForum = (requireArguments().get("subForum") as String)
        val question = (requireArguments().get("question") as String)
        factory = AnswersViewModelFactory(moduleName, subForum, question)
        viewModel = ViewModelProvider(this, factory).get(AnswersViewModel::class.java)
        // On start of activity, we load the user data to be display on dashboard later
        viewModel.loadPosts()
        viewModel.answers.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer<List<ForumComment>> { posts ->
                if (posts.size != 0) {
                    Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun configurePage(boolean: Boolean) {
        binding.moduleForumAnswersMessageHere.isEnabled = boolean
        binding.moduleForumAnswersSend.isEnabled = boolean
        binding.forumAnswersRecyclerView.isEnabled = boolean
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}