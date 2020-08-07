package com.orbital.snus.modules.Forum.Posts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import kotlinx.android.synthetic.main.module_forum_recycler_posts.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostViewAdapter (val bundle: Bundle, val forumList: List<ForumPost>) :
    RecyclerView.Adapter<PostViewAdapter.PostViewHolder>() {

    class PostViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun getItemCount() = forumList.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewAdapter.PostViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.module_forum_recycler_posts, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return PostViewAdapter.PostViewHolder(
            textView
        )
    }

    override fun onBindViewHolder(holder: PostViewAdapter.PostViewHolder, position: Int) {
        val post = forumList[position]
        val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/YYYY | hh:mm a")
        holder.textView.recycler_post_name.text = post.title
        holder.textView.recycler_post_date.text = showDate(post.date!!)
        holder.textView.recycler_post_resolved.setText(post.question)


        if (post.status == false) {
//            holder.textView.recycler_post_resolved.setTextColor(Color.RED)
            holder.textView.setOnClickListener(onClickListenerUnresolved(post))

        } else {
            holder.textView.setOnClickListener(onClickListenerResolved(post))

        }
    }

    private fun onClickListenerResolved(post: ForumPost): View.OnClickListener? {
        return View.OnClickListener {
            bundle.putParcelable("post", post)
            it.findNavController().navigate(R.id.action_postsFragment_to_questionFragment, bundle)
        }
    }

    private fun onClickListenerUnresolved(post: ForumPost): View.OnClickListener? {
        return View.OnClickListener {
            bundle.putParcelable("post", post)
            it.findNavController().navigate(R.id.action_postsUnresolvedFragment_to_questionFragment, bundle)
        }
    }

    fun resolved(status: Boolean, holder: PostViewAdapter.PostViewHolder) {
        if (status == false) {
            holder.textView.recycler_post_resolved.setText("Unresolved")
            holder.textView.recycler_post_resolved.setTextColor(Color.RED)
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
}