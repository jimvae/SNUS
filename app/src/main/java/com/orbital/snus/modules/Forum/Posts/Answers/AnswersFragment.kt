package com.orbital.snus.modules.Forum.Posts.Answers

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.ForumComment
import com.orbital.snus.data.FriendsMessage
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ModuleForumAnswersBinding
import com.orbital.snus.messages.Messaging.showDate
import com.orbital.snus.modules.ModulesActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.messages_messaging_from_recycler.view.*
import kotlinx.android.synthetic.main.messages_messaging_to_recycler.view.*
import kotlinx.android.synthetic.main.module_forum_recycler_answers_from.view.*
import kotlinx.android.synthetic.main.module_forum_recycler_answers_to.view.*
import java.text.SimpleDateFormat
import java.util.*


class AnswersFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val user = firebaseAuth.currentUser?.uid


    private lateinit var binding: ModuleForumAnswersBinding
    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var module: String
    private lateinit var subForum: String
    private lateinit var question: String
    private lateinit var userData: UserData




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

        module = (requireArguments().get("module") as String)
        subForum = (requireArguments().get("subForum") as String)
        question = (requireArguments().get("question") as String)

        fetchUser()
        fetchAnswers()


        binding.forumAnswersRecyclerView.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(this.context)
            // specify an viewAdapter (see also next example)
            adapter = groupAdapter
        }



        binding.moduleForumAnswersSend.setOnClickListener {
            hideKeyboard(it)
            val text = binding.moduleForumAnswersMessageHere.text.toString()

            if (text == "") {
                Toast.makeText(requireContext(), "Please enter message", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }


            //create comment, add to data base
            addComment(text)
            binding.moduleForumAnswersMessageHere.setText("")
            hideKeyboard(it)

        }
        return binding.root
    }

    fun fetchUser() {
        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("AnswersFragment", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val user = it.toObject(UserData::class.java)
                        if (user != null) {
                            if (user.userID.equals(firebaseAuth.currentUser!!.uid)) {
                                userData = user
                            }
                        }
                    }


                }
            }
    }


    fun fetchAnswers(){
        db.collection("modules").document(module)
            .collection("forums").document(subForum)
            .collection("posts").document(question)
            .collection("comments")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("AnswersFragment", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    groupAdapter.clear()
                    val comments = ArrayList<ForumComment>()
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val individualComment = it.toObject(ForumComment::class.java)
                        if (individualComment != null) {
                            comments.add(individualComment)
                        }
                    }

                    comments.sortWith(compareBy { it -> it.date })
                    comments.forEach { eachComment ->
                        if (eachComment.userID == user) {
                            groupAdapter.add(CommentTo(eachComment))
                        } else {
                            groupAdapter.add(CommentFrom(eachComment))
                        }
                    }

                    binding.forumAnswersRecyclerView.scrollToPosition(groupAdapter.itemCount - 1)


                }
            }
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

    fun addComment(comment: String) {
        val calendar = Calendar.getInstance()
        val id = db
            .collection("modules").document(module)
            .collection("forums").document(subForum)
            .collection("posts").document(question)
            .collection("comments").document().id

        val answer: ForumComment = ForumComment(id,
            firebaseAuth.currentUser!!.uid, userData.forumName, calendar.time, comment)

        db.collection("modules").document(module)
            .collection("forums").document(subForum)
            .collection("posts").document(question)
            .collection("comments").document(id).set(answer)

        binding.forumAnswersRecyclerView.scrollToPosition(groupAdapter.itemCount - 1)

    }
}

class CommentTo(val comment: ForumComment) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.module_forum_recycler_answers_to
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.module_forum_recycler_answers_to_answer.text = comment.text.toString()
        viewHolder.itemView.module_forum_recycler_answers_to_date.text = showDate(comment.date!!)
        viewHolder.itemView.module_forum_recycler_answers_to_name.text = comment.forumName.toString()

    }

}

class CommentFrom(val comment: ForumComment) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.module_forum_recycler_answers_from
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.module_forum_recycler_answers_from_answer.text = comment.text.toString()
        viewHolder.itemView.module_forum_recycler_answers_from_date.text = showDate(comment.date!!)
        viewHolder.itemView.module_forum_recycler_answers_from_name.text = comment.forumName.toString()



    }

}

fun showDate(given: Date) : String {
    val dateFormatDay = SimpleDateFormat("EEEE")
    val dateFormatDayOfYear = SimpleDateFormat("dd/MM/yyyy")
    val dateFormatTime = SimpleDateFormat("hh:mma")
    val weekOfYear = SimpleDateFormat("w")


    val today = Calendar.getInstance().time
    val thisWeek = weekOfYear.format(today).toPattern().toString()
    val messageWeek = weekOfYear.format(given).toPattern().toString()

    if (dateFormatDayOfYear.format(today).toPattern().toString().equals(dateFormatDayOfYear.format(given).toPattern().toString())) {
        return dateFormatTime.format(given).toPattern().toString()

    } else if (thisWeek.equals(messageWeek)) {
        return dateFormatDay.format(given).toPattern().toString()
    } else {
        return dateFormatDayOfYear.format(given).toPattern().toString()
    }

}