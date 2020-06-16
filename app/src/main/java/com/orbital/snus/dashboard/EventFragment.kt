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

class EventFragment() : Fragment() {
    private lateinit var binding: FragmentDashboardEventBinding

    val factory = DashboardDataViewModelFactory()
    private lateinit var viewModel: DashboardDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as DashboardActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_event, container, false
        )

        val event = requireArguments().get("event") as UserEvent

        val dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM, hh:mm a ")
        binding.popUpEventName.text = event.eventName
        binding.popUpEventDescription.text = event.eventDescription
        binding.popUpEventStartDate.text = dateFormatter.format(event.startDate).toPattern().toString()
        binding.popUpEventEndDate.text = dateFormatter.format(event.endDate).toPattern().toString()
        binding.popUpEventLocation.text = event.location

        viewModel = ViewModelProvider(this, factory).get(DashboardDataViewModel::class.java)

        binding.popUpDeleteButton.setOnClickListener {
            // delete from database
            viewModel.deleteEvent(event.id!!)

            configurePage(false)
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
            // navigate back to today fragment
        }
        binding.popUpEdit.setOnClickListener {
            // edit something
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
}