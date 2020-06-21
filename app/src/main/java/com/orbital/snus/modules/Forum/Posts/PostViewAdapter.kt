package com.orbital.snus.modules.Forum.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.modules.Forum.MainPage.MainPageAdapter
import kotlinx.android.synthetic.main.module_forum_recycler_enrolled_individual.view.*
import kotlinx.android.synthetic.main.module_forum_recycler_posts.view.*
import java.text.SimpleDateFormat

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
        holder.textView.recycler_post_poster.text = "Author ID Here (I haven't add)"
        holder.textView.recycler_post_date.text = dateFormatter.format(post.date!!).toPattern().toString()

        holder.textView.setOnClickListener(onClickListener(post))
    }

    private fun onClickListener(post: ForumPost): View.OnClickListener? {
        return View.OnClickListener {
            bundle.putParcelable("post", post)
            it.findNavController().navigate(R.id.action_postsFragment_to_questionFragment, bundle)
        }
    }
}