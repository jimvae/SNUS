package com.orbital.snus.dashboard.Today

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.orbital.snus.dashboard.DashboardViewModel
import com.orbital.snus.data.UserEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodayViewModel : DashboardViewModel() {

    fun filter(events : List<UserEvent>) : List<UserEvent> {
        val todayEvents = ArrayList<UserEvent>()
        for (item in events) {
            if (checkIfToday(item)) {
                todayEvents.add(item)
            }
        }
        return todayEvents
    }

    // Will exclude events that has finished before current instance in time
    fun checkIfToday(event: UserEvent) : Boolean {
        // need to check if event.StartDate <= Today <= event.End
        val fmt = SimpleDateFormat("yyyyMMdd")
        val todayDate = Calendar.getInstance().time
        val startDate = event.startDate!!
        val endDate = event.endDate!!

        //either todaydate is in between current
        // the today date = start date or  today date = end date
        return (startDate.compareTo(todayDate) <= 0 && todayDate.compareTo(endDate) <= 0) || fmt.format(todayDate) == fmt.format(startDate) || fmt.format(todayDate) == fmt.format(endDate)
    }
}

