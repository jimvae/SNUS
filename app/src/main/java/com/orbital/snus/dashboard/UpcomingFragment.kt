package com.orbital.snus.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.orbital.snus.R
import com.orbital.snus.databinding.FragmentDashboardUpcomingBinding

class UpcomingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentDashboardUpcomingBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard_upcoming, container, false
        )

        binding.buttonToday.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_upcomingFragment_to_todayFragment)
        }
        binding.buttonCalendar.setOnClickListener {
                view: View -> view.findNavController().navigate(R.id.action_upcomingFragment_to_calendarFragment)
        }
        return binding.root
    }
}