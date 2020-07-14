package com.orbital.snus.profile.MainTimeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.*

class MainTimelineViewModel(val user: UserData, val currentUser: UserData) : ViewModel() {

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
            .document(user.userID!!)
            .collection("timeline")
            .document().id

        timelinePost.id = id

        db.collection("users")
            .document(user.userID!!)
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
            .document(user.userID!!)
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
            .document(user.userID!!)
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
            .document(user.userID!!)
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

    // ADD POSTS
    private val _sendSuccess = MutableLiveData<Boolean?>()
    val sendSuccess: LiveData<Boolean?>
        get() = _sendSuccess

    private val _sendFailure = MutableLiveData<Exception?>()
    val sendFailure: LiveData<Exception?>
        get() = _sendFailure

    fun sendRequest(userFriendRequest: UserFriendRequest) {
        // from -> add to into from's requested
        // to -> add from into to's requests
        val id = db.collection("requests").document().id
        userFriendRequest.id = id
        db.collection("requests").document(id).set(userFriendRequest)
            .addOnSuccessListener {
                _sendSuccess.value = true
            }.addOnFailureListener {
                _sendFailure.value = it
            }
    }

    fun sendSuccessCompleted() {
        _sendSuccess.value = null
    }

    fun sendFailureCompleted() {
        _sendFailure.value = null
    }

    fun acceptRequest(userFriendRequest: UserFriendRequest) {
        // from -> delete to from from's requested, add to into friends
        // to -> delete from from to's requests, add from into friends
    }

    fun declineRequest(userFriendRequest: UserFriendRequest) {
        // from -> delete to from from's requested
        // to -> delete from from to's requests
    }

    val _userStatus = MutableLiveData<String>()
    val userStatus : LiveData<String>
        get() = _userStatus

    fun getUserStatus(userid: String) {
        // check if user is in friends list -> return "Friends"
        // check if user is in requested list -> return "Friend Request sent"
        // check if user is in requests list -> return "Friend Request sent to you!"
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        _userStatus.value = "Add Friend"

        db.collection("requests")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachRequest = it.toObject(UserFriendRequest::class.java)
                        if (eachRequest != null) {
                            // check if currentUser is sender, and userID is receiver
                            if (eachRequest.fromID.equals(currentUserID) && eachRequest.toID.equals(userid)) {
                                _userStatus.value = "Friend Request Sent!"
                            } else if (eachRequest.fromID.equals(userid) && eachRequest.toID.equals(currentUserID)) {
                                _userStatus.value = "Friend Request Sent to You!"
                            }
                        }
                    }
                }
            }

        db.collection("users")
            .document(currentUserID)
            .collection("friends")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachFriend = it.toObject(Friends::class.java)
                        if (eachFriend != null) {
                            if (eachFriend.friendID.equals(userid)) {
                                _userStatus.value = "Friends"
                            }
                        }
                    }
                }
            }
    }

    private val _delFailureReq = MutableLiveData<Exception?>()
    val delFailureReq: LiveData<Exception?>
        get() = _delFailureReq

    private val _delSuccessReq = MutableLiveData<Boolean?>()
    val delSuccessReq: LiveData<Boolean?>
        get() = _delSuccessReq

    fun delSuccessReqCompleted() {
        _delSuccessReq.value = null
    }

    fun delFailureReqCompleted() {
        _delFailureReq.value = null
    }


    fun declineMyRequest (id: String) {
        var requestID : String
        db.collection("requests")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachRequest = it.toObject(UserFriendRequest::class.java)
                        if (eachRequest != null) {
                            // check if currentUser is sender, and userID is receiver
                            if (eachRequest.fromID.equals(currentUser.userID) && eachRequest.toID.equals(user.userID)) {
                                requestID = eachRequest.id!!

                                db.collection("requests").document(requestID).delete()
                                    .addOnSuccessListener {
                                        _delSuccessReq.value = true
                                    }.addOnFailureListener {
                                        _delFailureReq.value = it
                                    }

                            }
                        }
                    }
                }
            }
    }

    private val _delFailureFriend = MutableLiveData<Exception?>()
    val delFailureFriend: LiveData<Exception?>
        get() = _delFailureFriend

    private val _delSuccessFriend = MutableLiveData<Boolean?>()
    val delSuccessFriend: LiveData<Boolean?>
        get() = _delSuccessFriend

    fun deleteFriend() {
        // delete from user's friends
        // delete from current user's (my) friends

        db.collection("users").document(user.userID!!).collection("friends").document(currentUser.userID!!).delete()
            .addOnSuccessListener {
                _delSuccessFriend.value = true
            }
            .addOnFailureListener {
                _delFailureFriend.value = it
            }
        db.collection("users").document(currentUser.userID).collection("friends").document(user.userID).delete()
            .addOnSuccessListener {
                _delSuccessFriend.value = true
            }
            .addOnFailureListener {
                _delFailureFriend.value = it
            }
    }

    fun delSuccessFriendCompleted() {
        _delSuccessFriend.value = null
    }

    fun delFailureFriendCompleted() {
        _delFailureFriend.value = null
    }
}

