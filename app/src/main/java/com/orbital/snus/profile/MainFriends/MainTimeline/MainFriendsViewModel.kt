package com.orbital.snus.profile.MainTimeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserFriendRequest
import java.util.*
import kotlin.collections.ArrayList

class MainFriendsViewModel(val user: UserData) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // all users in db
    private val _users = MutableLiveData<List<UserData>>()
    val users: LiveData<List<UserData>>
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
    private val _requests = MutableLiveData<List<UserData>>()
    val requests: LiveData<List<UserData>>
        get() = _requests

    // for requested
    private val _requested = MutableLiveData<List<UserData>>()
    val requested: LiveData<List<UserData>>
        get() = _requested

    // for search
    fun filterUsers(username: String) {
        val filteredList = ArrayList<UserData>()

        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainTimeLineViewModel", firebaseFirestoreException.toString())
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

    // for all users
    fun loadUsers() {
        val listUsers = ArrayList<UserData>()
        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("MainTimeLineViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachUser = it.toObject(UserData::class.java)
                        if (eachUser != null) {
                            listUsers.add(eachUser)
                        }
                    }
                }
                _users.value = listUsers
            }

    }

    fun sendRequest(userFriendRequest: UserFriendRequest) {
        // from -> add to into from's requested
        // to -> add from into to's requests
    }

    fun acceptRequest(userFriendRequest: UserFriendRequest) {
        // from -> delete to from from's requested, add to into friends
        // to -> delete from from to's requests, add from into friends
    }

    fun declineRequest(userFriendRequest: UserFriendRequest) {
        // from -> delete to from from's requested
        // to -> delete from from to's requests
    }

    fun getUserStatus(userid: String) : String {
        // check if user is in friends list -> return "Friends"
        // check if user is in requested list -> return "Friend Request sent"
        // check if user is in requests list -> return "Friend Request sent to you!"
        return "Add Friend"
    }
}