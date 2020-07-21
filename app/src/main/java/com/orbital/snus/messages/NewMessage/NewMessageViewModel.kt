package com.orbital.snus.messages.NewMessage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.Friends
import com.orbital.snus.data.UserData
import java.util.HashMap

class NewMessageViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    // all users in db
    private val _users = MutableLiveData<HashMap<String, UserData>>()
    val users: LiveData<HashMap<String, UserData>>
        get() = _users

    // for friends
    private val _friends = MutableLiveData<List<UserData>>()
    val friends: LiveData<List<UserData>>
        get() = _friends

    // for all users
    fun loadUsers() {
        val listUsers = HashMap<String, UserData>()
        db.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("NewMessageViewModel", firebaseFirestoreException.toString())
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
            .document(firebaseAuth.currentUser!!.uid)
            .collection("friends")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("NewMessageViewModel", firebaseFirestoreException.toString())
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


}