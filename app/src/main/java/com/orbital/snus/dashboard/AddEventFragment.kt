package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentDashboardAddEventBinding
import com.orbital.snus.data.UserEvent
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment() : Fragment() {

    private lateinit var viewModel : EventViewModel

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as DashboardActivity).hideNavBar()

        val binding: FragmentDashboardAddEventBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_add_event, container, false
        )

        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)

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

        // Confirm button
        binding.buttonConfirm.setOnClickListener {
            val name = binding.textEditEventName.text.toString()
            val description = binding.textEditEventDescription.text.toString()
            val location = binding.textEditEventLocation.text.toString()

            // Checking if data is legit
            if (name == "") {
                Toast.makeText(requireContext(), "Please enter event name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (startDate == null) {
                Toast.makeText(requireContext(), "Please enter start date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (endDate == null) {
                Toast.makeText(requireContext(), "Please enter end date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // disabling page
            binding.textEditEventName.isEnabled = false
            binding.textEditEventLocation.isEnabled = false
            binding.textEditEventDescription.isEnabled = false
            binding.buttonAddToTimeline.isEnabled = false
            binding.buttonConfirm.isEnabled = false
            binding.textStartDate.isEnabled = false
            binding.textEndDate.isEnabled = false

            val event = UserEvent(name, description, startDate!!, endDate!!, location, false)
            viewModel.addEvent(event)

            // Failure to add into database
            viewModel.result.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    binding.textEditEventName.isEnabled = true
                    binding.textEditEventLocation.isEnabled = true
                    binding.textEditEventDescription.isEnabled = true
                    binding.buttonAddToTimeline.isEnabled = true
                    binding.buttonConfirm.isEnabled = true
                    binding.textStartDate.isEnabled = true
                    binding.textEndDate.isEnabled = true
                    viewModel.exceptionChecked()
                } else {
                    Toast.makeText(requireContext(), "Event successfully added", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addEventFragment_to_todayFragment)
                }
            })
        }

        binding.textEditEventLocation.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
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
                        v.text = dateFormatter.format(startDate).toPattern().toString()
                    }
                    END -> {
                        endDate = c.time
                        v.text = dateFormatter.format(endDate).toPattern().toString()
                    }
                }
            },
            mHour,
            mMinute,
            true
        )

        // DATEPICKER
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                c.set(mYear, mMonth, mDay, mHour, mMinute)

                when (indicator) {
                    START -> {
                        startDate = c.time
                        v.text = dateFormatter.format(startDate).toPattern().toString()
                    }
                    END -> {
                        endDate = c.time
                        v.text = dateFormatter.format(endDate).toPattern().toString()
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
}
