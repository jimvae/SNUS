package com.orbital.snus.profile.MainTimeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.ForumPost
import com.orbital.snus.data.TimeLinePost

class MainTimelineViewModel() : ViewModel() {
    private val user = FirebaseAuth.getInstance().currentUser!!

    private val db = FirebaseFirestore.getInstance()

    private val _timelinePosts = MutableLiveData<List<TimeLinePost>>()
    val timelinePosts: LiveData<List<TimeLinePost>>
        get() = _timelinePosts

    // ADD POSTS
    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess: LiveData<Boolean?>
        get() = _addSuccess

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure: LiveData<Exception?>
        get() = _addFailure

    fun addTimeline(timelinePost: TimeLinePost) {
        val id = db.collection("users")
            .document(user.uid)
            .collection("timeline")
            .document().id

        timelinePost.id = id

        db.collection("users")
            .document(user.uid)
            .collection("timeline")
            .document(id).set(timelinePost)
            .addOnSuccessListener {
                _addSuccess.value = true
            }.addOnFailureListener {
                _addFailure.value = it
            }
    }

    fun addPostSuccessCompleted() {
        _addSuccess.value = null
    }

    fun addPostFailureCompleted() {
        _addFailure.value = null
    }

    // FOR DELETING POSTS
    private val _delSuccess = MutableLiveData<Boolean?>()
    val delSuccess: LiveData<Boolean?>
        get() = _delSuccess

    private val _delFailure = MutableLiveData<Exception?>()
    val delFailure: LiveData<Exception?>
        get() = _delFailure

    fun deletePost(ID: String) {
        db.collection("users")
            .document(user.uid)
            .collection("timeline")
            .document(ID).delete()
            .addOnSuccessListener {
                _delSuccess.value = true
                Log.d("Delete Post", "DocumentSnapshot successfully deleted!")
            }.addOnFailureListener {
                _delFailure.value = it
                Log.w("Delete Post", "Error deleting document", it)
            }
    }

    fun delPostSuccessCompleted() {
        _delSuccess.value = null
    }

    fun delPostFailureCompleted() {
        _delFailure.value = null
    }

    fun loadPosts() {
        val posts = ArrayList<TimeLinePost>()
        db.collection("users")
            .document(user.uid)
            .collection("timeline")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainTimeLineViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val currentPost = it.toObject(TimeLinePost::class.java)
                        if (currentPost != null) {
                            posts.add(currentPost)
                        }
                    }
                }
                posts.sortByDescending { it.date }
                _timelinePosts.value = posts
            }
    }

    // FOR UPDATING EVENTS

    private val _updateSuccess = MutableLiveData<Boolean?>()
    val updateSuccess: LiveData<Boolean?>
        get() = _updateSuccess

    private val _updateFailure = MutableLiveData<Exception?>()
    val updateFailure: LiveData<Exception?>
        get() = _updateFailure

    fun updatePost(timelinePost: TimeLinePost) {
        val id = timelinePost.id!!

        db.collection("users")
            .document(user.uid)
            .collection("timeline")
            .document(id).set(timelinePost)
            .addOnSuccessListener {
                _updateSuccess.value = true
            }.addOnFailureListener {
                _updateFailure.value = it
            }
    }

    fun updatePostSuccessCompleted() {
        _updateSuccess.value = null
    }

    fun updatePostFailureCompleted() {
        _updateFailure.value = null
    }
}

