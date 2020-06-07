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

        binding.textStartTime.setOnClickListener {
            setTime(it as TextView)
        }

        binding.textEndTime.setOnClickListener {
            setTime(it as TextView)
        }


        binding.buttonConfirm
        return binding.root
    }


    private fun setDate(v: TextView) {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                v.setText(
                    dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                )
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }


    private fun setTime(v: TextView) {
        // Get Current Time
        val c = Calendar.getInstance()
        val mHour = c[Calendar.HOUR_OF_DAY]
        val mMinute = c[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(
            this.requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> v.setText("$hourOfDay:$minute") },
            mHour,
            mMinute,
            false
        )
        timePickerDialog.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as DashboardActivity).showNavBar()
    }

}



