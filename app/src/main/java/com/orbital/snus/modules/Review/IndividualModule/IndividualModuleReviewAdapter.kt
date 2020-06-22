package com.orbital.snus.modules.Review.IndividualModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserReview
import kotlinx.android.synthetic.main.module_reviews_review_recycler.view.*
import java.text.SimpleDateFormat

class IndividualModuleReviewAdapter(val reviewList: List<UserReview>) :
    RecyclerView.Adapter<IndividualModuleReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    override fun getItemCount() = reviewList.size


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IndividualModuleReviewAdapter.ReviewViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.module_reviews_review_recycler, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return IndividualModuleReviewAdapter.ReviewViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("dd MMM")


        holder.textView.reviews_recycler_title.setText(review.title)

        holder.textView.reviews_recycler_date.setText(dateFormatter1.format(review.date!!).toPattern().toString())
        holder.textView.reviews_recycler_ratings.setText(review.rating.toString())
    }

//    private fun onClickListener(subForum: String): View.OnClickListener? {
//        return View.OnClickListener {
//            val bundle = Bundle()
//            bundle.putString("module", moduleName)
//            bundle.putString("subForum", subForum)
//            it.findNavController().navigate(R.id.action_individualModuleFragment_to_postsFragment, bundle)
//        }
    }
