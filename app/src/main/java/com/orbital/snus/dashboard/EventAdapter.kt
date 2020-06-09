package com.orbital.snus.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserEvent
import kotlinx.android.synthetic.main.event_daily_view.view.*
import java.text.SimpleDateFormat

// EventAdapter takes in the data, converts into the view that is to be displayed by the RecyclerView

class EventAdapter(eventList : List<UserEvent>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    val eventList = eventList

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class EventViewHolder(val textView: View) : RecyclerView.ViewHolder(textView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): EventAdapter.EventViewHolder {
        // create a new view
        // EventDailyView shows the layout of each view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_daily_view, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return EventViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    // Connect the data to the view
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val dateFormatter: SimpleDateFormat = SimpleDateFormat("hh:mm a")

        holder.textView.event_name.text = eventList[position].eventName
        holder.textView.event_description.text = eventList[position].eventDescription
        holder.textView.start_date.text = dateFormatter.format(eventList[position].startDate!!).toPattern().toString()
        holder.textView.end_date.text = dateFormatter.format(eventList[position].endDate!!).toPattern().toString()
        holder.textView.event_location.text = eventList[position].location
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = eventList.size
}