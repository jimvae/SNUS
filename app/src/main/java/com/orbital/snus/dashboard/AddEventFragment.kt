package com.orbital.snus.dashboard

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

        val binding: FragmentDashboardAddEventBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_add_event, container, false
        )


        binding.textStartDate.setOnClickListener {
            val c = Calendar.getInstance()
//            DashboardActivity().setDate(it as EditText)

        }
        binding.textEndDate.setOnClickListener {
            val c = Calendar.getInstance()
//            DashboardActivity().setDate(it as EditText)
        }

        binding.textStartTime.setOnClickListener {
//            DashboardActivity().setTime(it as EditText)
        }

        binding.textEndTime.setOnClickListener {
//            DashboardActivity().setTime(it as EditText)
        }

        binding.buttonConfirm
        return binding.root
    }

//    private fun setDate(v: EditText) {
//        val c = Calendar.getInstance()
//        val mYear = c[Calendar.YEAR]
//        val mMonth = c[Calendar.MONTH]
//        val mDay = c[Calendar.DAY_OF_MONTH]
//        val datePickerDialog = DatePickerDialog(
//            targetFragment,
//            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                v.setText(
//                    dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
//                )
//            }, mYear, mMonth, mDay
//        )
//        datePickerDialog.show()
//    }
//
//    private fun setTime(v: EditText) {
//        // Get Current Time
//        val c = Calendar.getInstance()
//        val mHour = c[Calendar.HOUR_OF_DAY]
//        val mMinute = c[Calendar.MINUTE]
//        val timePickerDialog = TimePickerDialog(
//            DashboardActivity(),
//            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> v.setText("$hourOfDay:$minute") },
//            mHour,
//            mMinute,
//            false
//        )
//        timePickerDialog.show()
//
//    }

}



