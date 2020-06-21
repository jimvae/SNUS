package com.orbital.snus.modules.Forum.Posts.Answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
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

        val dateFormatter: SimpleDateFormat = SimpleDateFormat("hh:mm a")

        holder.textView.module_forum_recycler_answers_answer.text = answer.text
        holder.textView.module_forum_recycler_answers_name.text = "Author Random ID"
        holder.textView.module_forum_recycler_answers_date.text = dateFormatter.format(answer.date!!).toPattern().toString()

    }

    public fun getComment(position: Int) : ForumComment {
        return answerList.get(position)
    }
}