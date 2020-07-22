package com.orbital.snus.modules.Review

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.ReviewThreadComment
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewIndividualReviewThreadBinding
import com.orbital.snus.modules.ModulesActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.messages_messaging_from_recycler.view.*
import kotlinx.android.synthetic.main.messages_messaging_to_recycler.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class IndividualReviewThreadFragment : Fragment() {

    private lateinit var binding: ModuleReviewIndividualReviewThreadBinding
    private lateinit var review: UserReview
    private lateinit var module: String

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(inflater, R.layout.module_review_individual_review_thread, container, false)

        review = requireArguments().get("review") as UserReview
        module = requireArguments().get("module") as String

        loadThread(review, module)

        binding.recyclerviewThread.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        binding.buttonSend.setOnClickListener {
            if (TextUtils.isEmpty(binding.editTextQuestion.text.toString())) {
                return@setOnClickListener
            }

            val text = binding.editTextQuestion.text.toString()
            val userID = firebaseAuth.currentUser!!.uid
            val date = Calendar.getInstance().time
            val threadID =
                db.collection("modules")
                .document(module)
                .collection("reviews")
                .document(review.id!!)
                .collection("thread").document().id

            val comment = ReviewThreadComment(threadID, userID, date, text)

            db.collection("modules")
                .document(module)
                .collection("reviews")
                .document(review.id!!)
                .collection("thread").document(threadID).set(comment)

            binding.editTextQuestion.setText("")
            hideKeyboard(it)

            binding.recyclerviewThread.scrollToPosition(groupAdapter.itemCount - 1)
        }

        return binding.root
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun loadThread(review: UserReview, module: String) {
        db.collection("modules")
            .document(module)
            .collection("reviews")
            .document(review.id!!)
            .collection("thread")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("IndividualReviewThread", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    groupAdapter.clear()
                    val comments = ArrayList<ReviewThreadComment>()
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachComment = it.toObject(ReviewThreadComment::class.java)
                        if (eachComment != null) {
                            comments.add(eachComment)
                        }
                    }

                    comments.sortWith(compareBy { it -> it.date })
                    comments.forEach {comment ->
                        if (comment.userID!!.equals(firebaseAuth.currentUser!!.uid)) {
                            groupAdapter.add(MyComment(comment))
                        } else {
                            groupAdapter.add(AnonymousComment(comment))
                        }
                    }

                    binding.recyclerviewThread.scrollToPosition(groupAdapter.itemCount - 1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ModulesActivity).showNavBar()
    }
}

class AnonymousComment (val comment: ReviewThreadComment) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.messages_messaging_from_recycler
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messages_messaging_recycler_message_from.text = comment.text.toString()
        viewHolder.itemView.messages_messaging_from_date.text =
            SimpleDateFormat("dd/MM/yyyy | hh:mm a").format(comment.date!!).toPattern().toString()
    }

}

class MyComment (val comment: ReviewThreadComment) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.messages_messaging_to_recycler
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messages_messaging_recycler_message_to.text = comment.text.toString()
        viewHolder.itemView.messages_messaging_recycler_to_date.text =
            SimpleDateFormat("dd/MM/yyyy | hh:mm a").format(comment.date!!).toPattern().toString()
    }

}