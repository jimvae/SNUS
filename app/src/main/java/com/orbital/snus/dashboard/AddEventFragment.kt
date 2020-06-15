package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.orbital.snus.R
import com.orbital.snus.dashboard.Today.TodayViewModel
import com.orbital.snus.dashboard.Today.TodayViewModelFactory
import com.orbital.snus.databinding.FragmentDashboardAddEventBinding
import com.orbital.snus.data.UserEvent
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment() : Fragment() {

    private lateinit var binding: FragmentDashboardAddEventBinding

    // Calendar and Date variables
    val c = Calendar.getInstance()
    var mYear = c[Calendar.YEAR]
    var mMonth = c[Calendar.MONTH]
    var mDay = c[Calendar.DAY_OF_MONTH]
    var mHour = c[Calendar.HOUR_OF_DAY]
    var mMinute = c[Calendar.MINUTE]

    var dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY'\n'hh:mm a")

    var startDate: Date? = null
    var endDate: Date? = null

    val START = "start"
    val END = "end"

    val factory = DashboardDataViewModelFactory()
    private lateinit var viewModel: DashboardDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as DashboardActivity).hideNavBar()


        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_add_event, container, false
        )

        viewModel = ViewModelProvider(this, factory).get(DashboardDataViewModel::class.java)

        // SET ON CLICK LISTENERS
        // Set start and end date/time
        binding.textStartDate.setOnClickListener {
            hideKeyboard(it)
            setDateAndTime(it as TextView, START)
        }
        binding.textEndDate.setOnClickListener {
            hideKeyboard(it)
            setDateAndTime(it as TextView, END)
        }

        binding.textEditEventLocation.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        // Confirm button
        binding.buttonConfirm.setOnClickListener {
            val name = binding.textEditEventName.text.toString()
            val description = binding.textEditEventDescription.text.toString()
            val location = binding.textEditEventLocation.text.toString()

            // Checking if data is legit
            if (name == "") {
                Toast.makeText(requireContext(), "Please enter event name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (startDate == null) {
                Toast.makeText(requireContext(), "Please enter start date", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (endDate == null) {
                Toast.makeText(requireContext(), "Please enter end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (startDate!!.compareTo(endDate!!) > 0) {
                Toast.makeText(requireContext(), "Start Date cannot be after End Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // disabling page
            configurePage(false)

            val event = UserEvent(name, description, startDate!!, endDate!!, location, false)
            viewModel.addEvent(event)
            viewModel.addSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Event successfully added", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_addEventFragment_to_todayFragment)
                    viewModel.addEventSuccessCompleted()
                }
            })
            viewModel.addFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.addEventFailureCompleted()
                }
            })
        }

        return binding.root
    }

    // Sets date and time in textView, and save the data in correct Date object following the indicator
    fun setDateAndTime (v: TextView, indicator: String) {
        // TIMEPICKER
        val timePickerDialog = TimePickerDialog(
            this.requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mHour = hourOfDay
                mMinute = minute
                c.set(mYear, mMonth, mDay, mHour, mMinute)

                when (indicator) {
                    START -> {
                        startDate = c.time
                        v.text = dateFormatter.format(startDate!!).toPattern().toString()
                    }
                    END -> {
                        endDate = c.time
                        v.text = dateFormatter.format(endDate!!).toPattern().toString()
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
                        startDate = c.time
                        v.text = dateFormatter.format(startDate!!).toPattern().toString()
                    }
                    END -> {
                        endDate = c.time
                        v.text = dateFormatter.format(endDate!!).toPattern().toString()
                    }
                }
            }, mYear, mMonth, mDay
        )

        timePickerDialog.show()
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as DashboardActivity).showNavBar()
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun configurePage(boolean: Boolean) {
        binding.textEditEventName.isEnabled = boolean
        binding.textEditEventLocation.isEnabled = boolean
        binding.textEditEventDescription.isEnabled = boolean
        binding.buttonAddToTimeline.isEnabled = boolean
        binding.buttonConfirm.isEnabled = boolean
        binding.textStartDate.isEnabled = boolean
        binding.textEndDate.isEnabled = boolean
    }
}
