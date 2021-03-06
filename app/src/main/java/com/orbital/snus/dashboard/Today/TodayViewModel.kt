package com.orbital.snus.dashboard.Today

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

class TodayViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _events = MutableLiveData<List<UserEvent>>()
    val events : LiveData<List<UserEvent>>
        get() = _events

    // fetching events from database
    fun loadUsers() {
        var eventList = ArrayList<UserEvent>()
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
                            if (checkIfToday(event)) {
                                eventList.add(event)
                            }
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

