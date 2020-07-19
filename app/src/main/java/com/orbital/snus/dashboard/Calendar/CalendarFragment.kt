package com.orbital.snus.dashboard.Calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardCalendarBinding
import kotlinx.android.synthetic.main.fragment_dashboard_calendar.*
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {
    private lateinit var viewModel: CalendarViewModel
    private var factory = CalendarViewModelFactory()
    private var events = ArrayList<UserEvent>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDashboardCalendarBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_calendar, container, false
        )

        // set up the recyclerView
        recyclerView = binding.calendarRecyclerView.apply{
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity)

            // specify an viewAdapter (see also next example)
            adapter = CalendarEventAdapter(events)
        }

        viewModel = ViewModelProvider(this, factory).get(CalendarViewModel::class.java)

        binding.buttonToday.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_calendarFragment_to_todayFragment)
        }
        binding.buttonUpcoming.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_calendarFragment_to_upcomingFragment)
        }
        binding.floatingButtonAdd.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_calendarFragment_to_addEventFragment)
        }


        binding.calendarView.setOnDateChangeListener {
            //calendarview, year , month , date
                _, i, il, i2 ->
            val currentDate = Calendar.getInstance()
            currentDate.set(i, il, i2)
            val todayEvents = viewModel.checkIfThisDate(currentDate.time)
            events.removeAll(events)
            events.addAll(todayEvents)
            recyclerView.adapter!!.notifyDataSetChanged()
        }




        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(CalendarViewModel::class.java)
        // On start of activity, we load the user data to be display on dashboard later
        viewModel.loadUsers()
        viewModel.events.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<UserEvent>> { eventList ->
            if (eventList.size != 0) {
                val currentDate = Calendar.getInstance()
                val todayEvents = viewModel.checkIfThisDate(currentDate.time)
                events.removeAll(events)
                events.addAll(todayEvents)
                recyclerView.adapter!!.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }
}