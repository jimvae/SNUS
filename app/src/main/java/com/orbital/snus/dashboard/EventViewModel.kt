package com.orbital.snus.dashboard

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.auth.User
import com.orbital.snus.data.UserEvent
import kotlinx.coroutines.Job
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KProperty

class EventViewModel : ViewModel() {
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
                            eventList.add(event)
                        }
                    }
                }
                _events.value = eventList
            }
    }
}


//    private val dbEvents =
//        db.collection("users").document(firebaseAuth.currentUser!!.uid)
//            .collection("events")
//

//
//    private val _event = MutableLiveData<UserEvent>()
//    val event : LiveData<UserEvent>
//        get() = _event
//
////    private val _result = MutableLiveData<Exception?>()
////    val result : LiveData<Exception?>
////        get() = _result
//
//    private val _result = MutableLiveData<Boolean?>(null)
//    val result : LiveData<Boolean?>
//        get() = _result
//
//    fun addEvent(event: UserEvent) {
//        // start to add inside database
//        val id = db.collection("users") // users collection
//            .document(firebaseAuth.currentUser!!.uid) // current userId
//            .collection("events") // user events collection
//            .document().id // event document with auto-generated key
//
//        event.id = id
//
//        db.collection("users") // users collection
//            .document(firebaseAuth.currentUser!!.uid) // current userId
//            .collection("events") // user events collection
//            .document(id).set(event) // event document with auto-generated key
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    _result.value = false
//                } else {
//                    _result.value = true
//                }
//            }
//    }
//
//    fun exceptionChecked() {
//        _result.value = null
//    }
//
//    // fetch data from database
//    fun fetchEvents() : List<UserEvent> {
//        val eventList = ArrayList<UserEvent>()
//        db.collection("users")
//            .document(firebaseAuth.currentUser!!.uid)
//            .collection("events")
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                if (firebaseFirestoreException != null) {
//                    Log.w("EventViewModel", firebaseFirestoreException.toString())
//                    return@addSnapshotListener
//                }
//
//                if (querySnapshot != null) {
//                    val documents = querySnapshot.documents
//                    documents.forEach {
//                        val event = it.toObject(UserEvent::class.java)
//                        if (event != null) {
//                            eventList.add(event)
//                        }
//                    }
//                }
//            }
//
//        return eventList
//    }
//}