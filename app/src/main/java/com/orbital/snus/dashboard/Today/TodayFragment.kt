package com.orbital.snus.dashboard.Today

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardViewModel
import com.orbital.snus.data.UserEvent
import com.orbital.snus.databinding.FragmentDashboardTodayBinding
import java.text.SimpleDateFormat
import java.util.*

class TodayFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val factory = TodayViewModelFactory()
    private lateinit var viewModel: TodayViewModel
    private val events = ArrayList<UserEvent>() // holder to store events and for RecyclerViewAdapter to observe

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDashboardTodayBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_today, container, false
        )

        binding.dateToday.text = getCurrentDate()

        firebaseAuth = FirebaseAuth.getInstance()
        viewManager = LinearLayoutManager(activity)
        viewAdapter = TodayEventAdapter(events)

        // set up the recyclerView
        recyclerView = binding.todayRecyclerView.apply {
            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
        viewModel = ViewModelProvider(this, factory).get(TodayViewModel::class.java)

        DashboardViewModel.events.observe(viewLifecycleOwner, Observer<List<UserEvent>> { dbEvents ->
            val output: List<UserEvent> = viewModel.filter(dbEvents)
            events.removeAll(events)
            events.addAll(output)
            recyclerView.adapter!!.notifyDataSetChanged()
        })


        binding.buttonUpcoming.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_todayFragment_to_upcomingFragment)
        }
        binding.buttonCalendar.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_todayFragment_to_calendarFragment)
        }
        binding.floatingButtonAdd.setOnClickListener {
            view: View -> view.findNavController().navigate(R.id.action_todayFragment_to_addEventFragment)
        }

        return binding.root
    }

    private fun getCurrentDate() : String {
        val dateFormatter = SimpleDateFormat("EEEE, MMMM dd ")
        val dateToday = Calendar.getInstance().time
        return " ${dateFormatter.format(dateToday).toPattern().toString()} "
    }
}