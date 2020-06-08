package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentDashboardAddEventBinding
import com.orbital.snus.data.UserEvent
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as DashboardActivity).hideNavBar()

        val binding: FragmentDashboardAddEventBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_add_event, container, false
        )

        binding.textStartDate.setOnClickListener {

            setDate(it as TextView)
        }
        binding.textEndDate.setOnClickListener {
            setDate(it as TextView)
        }



        binding.buttonConfirm.setOnClickListener {
            val eventName = binding.textEditEventName.toString()
            val eventDescription = binding.textEditEventDescription.toString()
            val startDate = binding.textStartDate.toString()
            val endDate = binding.textEndDate.toString()
//            val startTime = binding.textStartTime.toString()
//            val endTime = binding.textEndTime.toString()


//                    this.eventName = eventName
//            this.eventDescription = eventDescription
//            this.startDate = startDate
//            this.endDate = endDate
//            this.location = location
//            this.addToTimeline = addToTimeline
//            val event = UserEvent()
        }
        return binding.root
    }


    private fun setDate(v: TextView) : String {
        var dateString: String = ""
        val c = Calendar.getInstance()
        var mHour = c[Calendar.HOUR_OF_DAY]
        var mMinute = c[Calendar.MINUTE]
        var timePickerDialog = TimePickerDialog(
            this.requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                v.text =  "$mHour:$mMinute"
            },
            mHour,
            mMinute,
            false
        )
        timePickerDialog.show()

        var mYear = c[Calendar.YEAR]
        var mMonth = c[Calendar.MONTH]
        var mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                dateString = "$mDay ${mMonth + 1} - $mYear"
                v.text =  "${v.text} ${dateString}"
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()



//        val date: SimpleDateFormat = SimpleDateFormat()
//        val dateString: String = date.format(c.getTime())

        System.out.println(dateString)
        return dateString

    }


//    private fun setTime(v: TextView) {
//        // Get Current Time
//        val c = Calendar.getInstance()
//        val mHour = c[Calendar.HOUR_OF_DAY]
//        val mMinute = c[Calendar.MINUTE]
//        val timePickerDialog = TimePickerDialog(
//            this.requireContext(),
//            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> v.setText("$hourOfDay:$minute") },
//            mHour,
//            mMinute,
//            false
//        )
//        timePickerDialog.show()
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as DashboardActivity).showNavBar()
    }

}



