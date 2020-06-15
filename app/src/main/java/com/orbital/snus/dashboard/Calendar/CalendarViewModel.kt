package com.orbital.snus.dashboard.Calendar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.UserEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _events = MutableLiveData<List<UserEvent>>()
    val events : LiveData<List<UserEvent>>
        get() = _events

    // fetching events from database
    fun loadUsers() {
        val eventList = ArrayList<UserEvent>()
        db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("events")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("EventViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val event = it.toObject(UserEvent::class.java)
                        if (event != null) {
                            eventList.add(event)
                        }
                    }

                    // assuming the sorting is stable
                    eventList.sortWith(compareBy { it.endDate })
                    eventList.sortWith(compareBy { it.startDate })
                }
                _events.value = eventList
            }
    }

    // Will exclude events that has finished before current instance in time
    fun checkIfThisDate(date: Date): ArrayList<UserEvent> {
        val fmt = SimpleDateFormat("yyyyMMdd")
        val todayDate = date

        val todayEvents = ArrayList<UserEvent>()
        for (event in _events.value!!) {
            val startDate = event.startDate!!
            val endDate = event.endDate!!
            if ((startDate.compareTo(todayDate) <= 0 && todayDate.compareTo(endDate) <= 0) || fmt.format(todayDate) == fmt.format(startDate) || fmt.format(todayDate) == fmt.format(endDate))
                todayEvents.add(event)
        }
        return todayEvents
    }
}

