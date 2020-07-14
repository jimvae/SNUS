package com.orbital.snus.profile.MainTimeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.Friends
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserFriendRequest
import java.util.*
import kotlin.collections.ArrayList

class MainFriendsViewModel(val user: UserData, val currentUser: UserData) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // all users in db
    private val _users = MutableLiveData<HashMap<String,UserData>>()
    val users: LiveData<HashMap<String,UserData>>
        get() = _users

    // for search
    private val _filteredUsers = MutableLiveData<List<UserData>>()
    val filteredUsers: LiveData<List<UserData>>
        get() = _filteredUsers

    // for friends
    private val _friends = MutableLiveData<List<UserData>>()
    val friends: LiveData<List<UserData>>
        get() = _friends

    // for requests
    private val _requests = MutableLiveData<List<UserFriendRequest>>()
    val requests: LiveData<List<UserFriendRequest>>
        get() = _requests

    // for deleting requests
    private val _delRequestSuccess = MutableLiveData<Boolean?>()
    val delRequestSuccess: LiveData<Boolean?>
        get() = _delRequestSuccess

    private val _delFailure = MutableLiveData<Exception?>()
    val delFailure: LiveData<Exception?>
        get() = _delFailure


    private val _delFailureRequest = MutableLiveData<Exception?>()
    val delFailureRequest: LiveData<Exception?>
        get() = _delFailureRequest

    private val _delSuccessRequest = MutableLiveData<Boolean?>()
    val delSuccessRequest: LiveData<Boolean?>
        get() = _delSuccessRequest


    private val _addFailureRequest = MutableLiveData<Exception?>()
    val addFailureRequest: LiveData<Exception?>
        get() = _addFailureRequest

    private val _addSuccessRequest = MutableLiveData<Boolean?>()
    val addSuccessRequest: LiveData<Boolean?>
        get() = _addSuccessRequest
    // for searchx
    fun filterUsers(username: String) {
        val filteredList = ArrayList<UserData>()

        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachUser = it.toObject(UserData::class.java)
                        if (eachUser != null) {
                            if (eachUser.fullname!!.toUpperCase(Locale.ROOT).contains(username.toUpperCase(Locale.ROOT))) {
                                filteredList.add(eachUser)
                            }
                        }
                    }
                }
                _filteredUsers.value = filteredList
            }
    }

    fun loadRequests() {
        val requestList = ArrayList<UserFriendRequest>()

        db.collection("requests")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val request = it.toObject(UserFriendRequest::class.java)
                        if (request != null) {
                            if (request.toID.equals(user.userID)) {
                                        requestList.add(request)
                            }
                        }
                    }
                }
                _requests.value = requestList
            }
    }

    // for all users
    fun loadUsers() {
        val listUsers = HashMap<String, UserData>()
        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainFriendsViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachUser = it.toObject(UserData::class.java)
                        if (eachUser != null) {
                            listUsers.put(eachUser.userID!!, eachUser)
                        }
                    }
                }
                _users.value = listUsers
            }

    }

    fun loadFriends() {
        val listFriends = ArrayList<UserData>()
        db.collection("users")
            .document(user.userID!!)
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
                            if (_users.value!!.get(eachFriend.friendID) != null) {
                                listFriends.add(_users.value!!.get(eachFriend.friendID)!!)
                            }
                        }
                    }
                    _friends.value = listFriends
                }
            }
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

    fun acceptRequest(currentUserInfo: Friends, otherUserInfo: Friends) {
        db.collection("users").document(currentUserInfo.friendID!!).collection("friends").document(otherUserInfo.friendID!!).set(otherUserInfo)
            .addOnSuccessListener {
                _addSuccessRequest.value = true
            }.addOnFailureListener {
                _addFailureRequest.value = it
            }
        db.collection("users").document(otherUserInfo.friendID).collection("friends").document(currentUserInfo.friendID).set(currentUserInfo)
            .addOnSuccessListener {
                _addSuccessRequest.value = true
            }.addOnFailureListener {
                _addFailureRequest.value = it
            }

    }

    fun declineRequest(id: String) {
        db.collection("requests")
            .document(id).delete()
            .addOnSuccessListener {
                _delSuccessRequest.value = true
                Log.d("Delete Post", "DocumentSnapshot successfully deleted!")
            }.addOnFailureListener {
                _delFailureRequest.value = it
                Log.w("Delete Post", "Error deleting document", it)
            }

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
}