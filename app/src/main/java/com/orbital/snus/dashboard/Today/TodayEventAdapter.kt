package com.orbital.snus.dashboard.Today

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserEvent
import kotlinx.android.synthetic.main.event_daily_view.view.*
import java.text.SimpleDateFormat
import java.util.*


// EventAdapter takes in the data, converts into the view that is to be displayed by the RecyclerView

class TodayEventAdapter(eventList : List<UserEvent>) :
    RecyclerView.Adapter<TodayEventAdapter.EventViewHolder>() {

    val eventList = eventList

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
        val dateFormatter2: SimpleDateFormat = SimpleDateFormat("hh:mm a E")
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




        // - get element from your dataset at this position
        // - replace the contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = eventList.size

//    fun checkIfOnlyToday(event: UserEvent) : Boolean {
//        // need to check if event.StartDate <= Today <= event.End
//        val todayDate = Calendar.getInstance()
//        val startDate = event.startDate!!
//        val endDate = event.endDate!!
//        return (startDate.day - todayDate.) == 0 && (endDate.day - todayDate.day) == 0
//    }

    fun allDay(event: UserEvent): Boolean {

        val today: Date = Calendar.getInstance().time
        val endDate: Date = event.endDate!!
        val fmt = SimpleDateFormat("yyyyMMdd")
        return fmt.format(today) != fmt.format(endDate)
    }

}