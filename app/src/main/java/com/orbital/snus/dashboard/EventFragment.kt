package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.orbital.snus.R
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardEventBinding
import java.text.SimpleDateFormat
import java.util.*

class EventFragment() : Fragment() {
    private lateinit var binding: FragmentDashboardEventBinding

    val factory = DashboardDataViewModelFactory()
    private lateinit var viewModel: DashboardDataViewModel

    val event = requireArguments().get("event") as UserEvent

    val START = "start"
    val END = "end"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as DashboardActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_event, container, false
        )

        val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM, hh:mm a ")
        binding.popUpEventName.text = event.eventName
        binding.popUpEventDescription.text = event.eventDescription
        binding.popUpEventStartDate.text = dateFormatter.format(event.startDate).toPattern().toString()
        binding.popUpEventEndDate.text = dateFormatter.format(event.endDate).toPattern().toString()
        binding.popUpEventLocation.text = event.location

        viewModel = ViewModelProvider(this, factory).get(DashboardDataViewModel::class.java)

        binding.popUpDeleteButton.setOnClickListener {
            // delete from database
            configurePage(false)
            viewModel.deleteEvent(event.id!!)
            viewModel.delSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Event successfully deleted", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_eventFragment_to_todayFragment)
                    viewModel.delEventSuccessCompleted()
                }
            })
                viewModel.delFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    if (it != null) {
                        Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                        configurePage(true)
                        viewModel.delEventFailureCompleted()
                    }
                })
        }
        binding.popUpClose.setOnClickListener {

        }
        binding.popUpEdit.setOnClickListener {
            // edit event from dialog
            // update the event

            configurePage(false)
            viewModel.updateEvent(event)
            viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Event successfully updated", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updateEventSuccessCompleted()
                }
            })
            viewModel.updateFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.updateEventFailureCompleted()
                }
            })
            configurePage(true)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as DashboardActivity).showNavBar()
    }

    fun configurePage(boolean: Boolean) {
        binding.popUpEventName.isEnabled = boolean
        binding.popUpEventDescription.isEnabled = boolean
        binding.popUpEventLocation.isEnabled = boolean
        binding.popUpEventStartDate.isEnabled = boolean
        binding.popUpEventEndDate.isEnabled = boolean
        binding.popUpEdit.isEnabled = boolean
        binding.popUpClose.isEnabled = boolean
        binding.popUpDeleteButton.isEnabled = boolean
    }


    // Sets date and time in textView, and save the data in correct Date object following the indicator
    fun setDateAndTime (v: TextView, indicator: String) {

        // Calendar and Date variables
        val c = Calendar.getInstance()
        var mYear = c[Calendar.YEAR]
        var mMonth = c[Calendar.MONTH]
        var mDay = c[Calendar.DAY_OF_MONTH]
        var mHour = c[Calendar.HOUR_OF_DAY]
        var mMinute = c[Calendar.MINUTE]

        var dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY'\n'hh:mm a")

        // TIMEPICKER
        val timePickerDialog = TimePickerDialog(
            this.requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                c.set(mYear, mMonth, mDay, mHour, mMinute)

                when (indicator) {
                    START -> {
                        event.startDate = c.time
                        v.text = dateFormatter.format(event.startDate!!).toPattern().toString()
                    }
                    END -> {
                        event.endDate = c.time
                        v.text = dateFormatter.format(event.endDate!!).toPattern().toString()
                    }
                }
            },
            mHour,
            mMinute,
            false
        )

        // DATEPICKER
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                c.set(mYear, mMonth, mDay, mHour, mMinute)

                when (indicator) {
                    START -> {
                        event.startDate = c.time
                        v.text = dateFormatter.format(event.startDate!!).toPattern().toString()
                    }
                    END -> {
                        event.endDate = c.time
                        v.text = dateFormatter.format(event.endDate!!).toPattern().toString()
                    }
                }
            }, mYear, mMonth, mDay
        )

        timePickerDialog.show()
        datePickerDialog.show()
    }
}