package com.orbital.snus.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.auth.User
import com.orbital.snus.R
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardUpcomingBinding
import kotlinx.android.synthetic.main.fragment_dashboard_upcoming.*
import java.util.ArrayList

class UpcomingFragment : Fragment() {

    val factory = UpcomingViewModelFactory()
    private lateinit var viewModel: UpcomingViewModel

    private lateinit var recyclerViewSunday: RecyclerView
    private lateinit var recyclerViewMonday: RecyclerView
    private lateinit var recyclerViewTuesday: RecyclerView
    private lateinit var recyclerViewWednesday: RecyclerView
    private lateinit var recyclerViewThursday: RecyclerView
    private lateinit var recyclerViewFriday: RecyclerView
    private lateinit var recyclerViewSaturday: RecyclerView

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDashboardUpcomingBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_upcoming, container, false
        )

        viewModel = ViewModelProvider(this, factory).get(UpcomingViewModel::class.java)

        val eventSunday = ArrayList<UserEvent>()
        val eventMonday = ArrayList<UserEvent>()
        val eventTuesday = ArrayList<UserEvent>()
        val eventWednesday = ArrayList<UserEvent>()
        val eventThursday = ArrayList<UserEvent>()
        val eventFriday = ArrayList<UserEvent>()
        val eventSaturday = ArrayList<UserEvent>()
        // set up the recyclerView
        recyclerViewSunday = binding.recyclerViewSunday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventSunday)
        }
        recyclerViewMonday = binding.recyclerViewMonday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventMonday)
        }
        recyclerViewTuesday = binding.recyclerViewTuesday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventTuesday)

        }
        recyclerViewWednesday = binding.recyclerViewWednesday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventWednesday)

        }
        recyclerViewThursday = binding.recyclerViewThursday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventThursday)

        }
        recyclerViewFriday = binding.recyclerViewFriday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventFriday)

        }
        recyclerViewSaturday = binding.recyclerViewSaturday.apply {
            // use a linear layout manager
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            // specify an viewAdapter (see also next example)
            adapter = UpcomingEventAdapter(eventSaturday)

        }

        viewModel.events.observe(viewLifecycleOwner, Observer<List<UserEvent>> { dbEvents ->
            viewModel.filterEvents()
        })

        viewModel.eventSunday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventSunday.removeAll(eventSunday)
            eventSunday.addAll(it)
            recyclerViewSunday.adapter!!.notifyDataSetChanged()
        })

        viewModel.eventMonday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventMonday.removeAll(eventMonday)
            eventMonday.addAll(it)
            recyclerViewMonday.adapter!!.notifyDataSetChanged()
        })
        viewModel.eventTuesday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventTuesday.removeAll(eventTuesday)
            eventTuesday.addAll(it)
            recyclerViewTuesday.adapter!!.notifyDataSetChanged()
        })
        viewModel.eventWednesday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventWednesday.removeAll(eventWednesday)
            eventWednesday.addAll(it)
            recyclerViewWednesday.adapter!!.notifyDataSetChanged()
        })
        viewModel.eventThursday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventThursday.removeAll(eventThursday)
            eventThursday.addAll(it)
            recyclerViewThursday.adapter!!.notifyDataSetChanged()
        })
        viewModel.eventFriday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventFriday.removeAll(eventFriday)
            eventFriday.addAll(it)
            recyclerViewFriday.adapter!!.notifyDataSetChanged()
        })
        viewModel.eventSaturday.observe(viewLifecycleOwner, Observer<List<UserEvent>> {
            eventSaturday.removeAll(eventSaturday)
            eventSaturday.addAll(it)
            recyclerViewSaturday.adapter!!.notifyDataSetChanged()
        })

        binding.buttonToday.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_upcomingFragment_to_todayFragment)
        }
        binding.buttonCalendar.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_upcomingFragment_to_calendarFragment)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(UpcomingViewModel::class.java)
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