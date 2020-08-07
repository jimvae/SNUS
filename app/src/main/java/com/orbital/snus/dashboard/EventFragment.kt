package com.orbital.snus.dashboard

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.event_dialog_edit.*
import java.text.SimpleDateFormat
import java.util.*


class EventFragment() : Fragment() {
    private lateinit var binding: FragmentDashboardEventBinding

    val factory = DashboardDataViewModelFactory()
    private lateinit var viewModel: DashboardDataViewModel
    private lateinit var dialog: Dialog
    private lateinit var event: UserEvent

    var startDate: Date? = null
    var endDate: Date? = null
    val START = "start"
    val END = "end"
    val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM, hh:mm a ")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (activity as DashboardActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_event, container, false
        )
        event = requireArguments().get("event") as UserEvent
        viewModel = ViewModelProvider(this, factory).get(DashboardDataViewModel::class.java)

        initiateViews()

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

        binding.popUpEdit.setOnClickListener {
            dialog = Dialog(requireContext())
            showPopup(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as DashboardActivity).showNavBar()
    }

    fun configurePage(boolean: Boolean) {
        binding.popUpEdit.isEnabled = boolean
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

    fun showPopup(v: View?) {
        dialog.setContentView(R.layout.event_dialog_edit)

        // set up views
        val eventName = dialog.edit_event_name
        eventName.setText(event.eventName)

        val editStartDate = dialog.edit_event_start_date
        editStartDate.setText(dateFormatter.format(event.startDate).toPattern().toString())

        val editEndDate = dialog.edit_event_end_date
        editEndDate.setText(dateFormatter.format(event.endDate).toPattern().toString())

        val editDescription = dialog.edit_event_description
        editDescription.setText(event.eventDescription)

        val editLocation = dialog.edit_event_location
        editLocation.setText(event.location)

        // by default, the start and end date will take from event
        // override when clicked
        startDate = event.startDate
        endDate = event.endDate

        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.edit_close.setOnClickListener { dialog.dismiss() }
        dialog.edit_confirm_button.setOnClickListener {
            if (eventName.text.toString() == "") {
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

            // lock dialog
            configureDialog(false)
            configurePage(false)

            //updates event object
            event.updateEvent(eventName.text.toString(), editDescription.text.toString(),
                startDate!!, endDate!!, editLocation.text.toString())

            //updates backend
            viewModel.updateEvent(event)

            viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Event successfully updated", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updateEventSuccessCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)

                    //updates the field of events fragment
                    initiateViews()
                    dialog.dismiss()
                }
            })
            viewModel.updateFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.updateEventFailureCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)
                }
            })
        }

        dialog.edit_event_start_date.setOnClickListener {
            setDateAndTime(it as TextView, START)
        }

        dialog.edit_event_end_date.setOnClickListener {
            setDateAndTime(it as TextView, END)
        }
    }

    fun initiateViews() {
        binding.popUpEventName.text = event.eventName
        binding.popUpEventDescription.text = event.eventDescription

        binding.popUpEventLocation.text = event.location
        binding.popUpEventDate.text = "${dateFormatter.format(event.startDate!!).toPattern().toString()} - ${dateFormatter.format(event.endDate!!).toPattern().toString()}"
    }

    fun configureDialog(boolean: Boolean) {
        // prevent edits when confirmed
        dialog.edit_event_name.isEnabled = boolean
        dialog.edit_event_start_date.isEnabled = boolean
        dialog.edit_event_end_date.isEnabled = boolean
        dialog.edit_event_description.isEnabled = boolean
        dialog.edit_event_location.isEnabled = boolean

        dialog.edit_close.isEnabled = boolean
        dialog.edit_confirm_button.isEnabled = boolean
    }
}