package com.orbital.snus.dashboard.Today

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.dashboard.DashboardDataViewModel
import com.orbital.snus.data.UserEvent
import kotlinx.android.synthetic.main.event_daily_view.view.*
import java.text.SimpleDateFormat
import java.util.*


// EventAdapter takes in the data, converts into the view that is to be displayed by the RecyclerView

class TodayEventAdapter(val eventList: List<UserEvent>) :
    RecyclerView.Adapter<TodayEventAdapter.EventViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class EventViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): EventViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_daily_view, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return EventViewHolder(
            textView
        )
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Connect the data to the view
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("dd MMM")
        val dateFormatter3: SimpleDateFormat = SimpleDateFormat("hh:mm a ")


        val event = eventList[position]


        holder.textView.event_name.text = event.eventName
        holder.textView.event_description.text = event.eventDescription
        holder.textView.event_location.text = event.location

        if (!allDay(event)) {
            holder.textView.start_date.text =
                dateFormatter3.format(event.startDate!!).toPattern().toString()
            holder.textView.end_date.text =
                dateFormatter3.format(event.endDate!!).toPattern().toString()
        } else {

            holder.textView.start_date.text = "ends on ${dateFormatter1.format(event.endDate!!).toPattern().toString()}"
                        holder.textView.end_date.text = dateFormatter3.format(event.endDate!!).toPattern().toString()
        }

        holder.textView.setOnClickListener(onClickListener(position));
    }

    // Onclick of item, navigate to appropriate event fragment, passing in the event in the bundle
    private fun onClickListener(position: Int): View.OnClickListener? {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("event", eventList[position])
            it.findNavController().navigate(R.id.action_todayFragment_to_eventFragment, bundle)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = eventList.size

    private fun allDay(event: UserEvent): Boolean {

        val today: Date = Calendar.getInstance().time
        val endDate: Date = event.endDate!!
        val fmt = SimpleDateFormat("yyyyMMdd")
        return fmt.format(today) != fmt.format(endDate)
    }

}