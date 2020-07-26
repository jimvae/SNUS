package com.orbital.snus.profile.MainTimeline

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.ForumComment
import com.orbital.snus.data.ForumPost
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.modules.Forum.Posts.PostViewAdapter
import kotlinx.android.synthetic.main.module_forum_recycler_posts.view.*
import kotlinx.android.synthetic.main.profile_main_posts_recycler.view.*
import java.text.SimpleDateFormat
import java.util.*

class MainTimelineAdapter (val timelineList: List<TimeLinePost>) :
    RecyclerView.Adapter<MainTimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun getItemCount() = timelineList.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainTimelineAdapter.TimelineViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_main_posts_recycler, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return MainTimelineAdapter.TimelineViewHolder(
            textView
        )
    }

    override fun onBindViewHolder(holder: MainTimelineAdapter.TimelineViewHolder, position: Int) {
        val post = timelineList[position]
        val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/YYYY | hh:mm a")
        holder.textView.profile_timeline_recycler_title.text = post.title
        holder.textView.profile_timeline_recycler_date.text = showDate(post.date!!)
        holder.textView.profile_timeline_recycler_extra_details.text = post.details
    }

    public fun getPost(position: Int) : TimeLinePost {
        return timelineList.get(position)
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

