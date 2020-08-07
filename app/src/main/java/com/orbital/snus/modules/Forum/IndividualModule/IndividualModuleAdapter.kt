package com.orbital.snus.modules.Forum.IndividualModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import kotlinx.android.synthetic.main.module_forum_recycler_enrolled_individual.view.*
import kotlinx.android.synthetic.main.module_forum_recycler_enrolled_individual.view.Sub_forums_enrolled_individual_name
import kotlinx.android.synthetic.main.module_forum_recycler_sub_forums.view.*

class IndividualModuleAdapter (val moduleName: String, val subForums: List<String>) :
    RecyclerView.Adapter<IndividualModuleAdapter.SubForumViewHolder>() {

    class SubForumViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun getItemCount() = subForums.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IndividualModuleAdapter.SubForumViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.module_forum_recycler_sub_forums, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return IndividualModuleAdapter.SubForumViewHolder(textView)
    }

    override fun onBindViewHolder(holder: SubForumViewHolder, position: Int) {
        val subForum = subForums[position]

        holder.textView.Sub_forums_enrolled_individual_name.setText(subForum)

        holder.textView.setOnClickListener(onClickListener(subForum))
    }

    private fun onClickListener(subForum: String): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("module", moduleName)
            bundle.putString("subForum", subForum)
            it.findNavController().navigate(R.id.action_individualModuleFragment_to_postsFragment, bundle)
        }
    }
}