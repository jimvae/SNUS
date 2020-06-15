package com.orbital.snus.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.UserEvent

open class DashboardViewModel : ViewModel() {
    companion object handler {
        private val firebaseAuth = FirebaseAuth.getInstance()
        private val db = FirebaseFirestore.getInstance()

        private val _events = MutableLiveData<List<UserEvent>>()
        val events : LiveData<List<UserEvent>>
            get() = _events

        private val _addSuccess = MutableLiveData<Boolean?>()
        val addSuccess : LiveData<Boolean?>
            get() = _addSuccess

        private val _addFailure = MutableLiveData<Exception?>()
        val addFailure : LiveData<Exception?>
            get() = _addFailure

        fun addEvent(event: UserEvent) {
            // start to add inside database
            val id = db.collection("users") // users collection
                .document(firebaseAuth.currentUser!!.uid) // current userId
                .collection("events") // user events collection
                .document().id // event document with auto-generated key

            event.id = id

            db.collection("users") // users collection
                .document(firebaseAuth.currentUser!!.uid) // current userId
                .collection("events") // user events collection
                .document(id).set(event)
                .addOnSuccessListener {
                    _addSuccess.value = true
                }.addOnFailureListener {
                    _addFailure.value = it
                }
        }

        fun addEventSuccessCompleted() {
            _addSuccess.value = null
        }

        fun addEventFailureCompleted() {
            _addFailure.value = null
        }

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
    }
}