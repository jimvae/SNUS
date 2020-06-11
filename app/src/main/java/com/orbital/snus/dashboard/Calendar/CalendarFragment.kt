package com.orbital.snus.dashboard.Calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.dashboard.Today.TodayEventAdapter
import com.orbital.snus.dashboard.Today.TodayViewModel
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardCalendarBinding
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {
    private lateinit var viewModel: CalendarViewModel
    private var events = ArrayList<UserEvent>()



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDashboardCalendarBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_calendar, container, false
        )

        var viewAdapter = TodayEventAdapter(events)
        var factory = CalendarViewModelFactory()

        // set up the recyclerView
        var recyclerView = binding.calendarRecyclerView.apply{
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity)

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

        viewModel = ViewModelProvider(this, factory).get(CalendarViewModel::class.java)

//        viewModel.events.observe(viewLifecycleOwner, Observer<List<UserEvent>> { dbEvents ->
//            events.removeAll(events)
//            events.addAll(dbEvents)
//            recyclerView.adapter!!.notifyDataSetChanged()
//        })


        binding.buttonToday.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_calendarFragment_to_todayFragment)
        }
        binding.buttonUpcoming.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_calendarFragment_to_upcomingFragment)
        }




        binding.calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener {
            //calendarview, year , month , date
                _, i, il, i2 ->
            val currentDate = Calendar.getInstance()
            currentDate.set(i, il, i2)
            print(currentDate.time)
            events.removeAll(events)
            val todayEvents = viewModel.checkIfThisDate(currentDate.time)
            events.addAll(todayEvents)
            recyclerView.adapter!!.notifyDataSetChanged()

        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var factory = CalendarViewModelFactory()
        viewModel = ViewModelProvider(this, factory).get(CalendarViewModel::class.java)
        // On start of activity, we load the user data to be display on dashboard later
        viewModel.loadUsers()
        viewModel.events.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<UserEvent>> { events ->
            if (events.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }
}