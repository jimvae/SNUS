package com.orbital.snus.modules.Forum.Posts.Answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.ForumComment
import com.orbital.snus.data.ForumPost
import com.orbital.snus.modules.Forum.MainPage.MainPageAdapter
import kotlinx.android.synthetic.main.module_forum_recycler_answers.view.*
import kotlinx.android.synthetic.main.module_forum_recycler_enrolled_individual.view.*
import kotlinx.android.synthetic.main.module_forum_recycler_posts.view.*
import java.text.SimpleDateFormat

class AnswersAdapter (val answerList: List<ForumComment>) :

    RecyclerView.Adapter<AnswersAdapter.AnswersHolder>() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val userID = firebaseAuth.currentUser!!.uid



    class AnswersHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun getItemCount() = answerList.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnswersAdapter.AnswersHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.module_forum_recycler_answers, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return AnswersAdapter.AnswersHolder(
            textView
        )
    }

    override fun onBindViewHolder(holder: AnswersAdapter.AnswersHolder, position: Int) {
        val answer = answerList[position]

        // Should the format include date? if not the display a bit weird
        val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy | hh:mm a")

        if (answer.userID == userID) {
            holder.textView.module_forum_recycler_answers_your_answer.text = answer.text
            holder.textView.module_forum_recycler_answers_your_name.text = "You"
            holder.textView.module_forum_recycler_answers_your_date.text =
                dateFormatter.format(answer.date!!).toPattern().toString()
            holder.textView.module_forum_recycler_answers_answer.isEnabled = false
            holder.textView.module_forum_recycler_answers_answer.isVisible = false
            holder.textView.module_forum_recycler_answers_date.isEnabled = false
            holder.textView.module_forum_recycler_answers_date.isVisible = false
            holder.textView.module_forum_recycler_answers_name.isEnabled = false
            holder.textView.module_forum_recycler_answers_name.isVisible = false

        } else {
            holder.textView.module_forum_recycler_answers_answer.text = answer.text
            holder.textView.module_forum_recycler_answers_name.text = "#ID"
            holder.textView.module_forum_recycler_answers_date.text =
                dateFormatter.format(answer.date!!).toPattern().toString()

            holder.textView.module_forum_recycler_answers_your_answer.isEnabled = false
            holder.textView.module_forum_recycler_answers_your_answer.isVisible = false
            holder.textView.module_forum_recycler_answers_your_date.isEnabled = false
            holder.textView.module_forum_recycler_answers_your_date.isVisible = false
            holder.textView.module_forum_recycler_answers_your_name.isEnabled = false
            holder.textView.module_forum_recycler_answers_your_name.isVisible = false


        }


    }

    public fun getComment(position: Int) : ForumComment {
        return answerList.get(position)
    }
}