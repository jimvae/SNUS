package com.orbital.snus.messages.MainPage


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.Friends
import com.orbital.snus.data.FriendsMessage
import com.orbital.snus.data.UserData
import kotlinx.android.synthetic.main.messages_friends_list_recycler.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessagesMainPageViewModel : ViewModel() {

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

    fun loadLatestMessage(user: UserData, holder: MessagesMainPageAdapter.UserViewHolder) {
        val myID = FirebaseAuth.getInstance().currentUser!!.uid
        val messages = ArrayList<FriendsMessage>()

        val friendID = user.userID!!
        db.collection("users").document(myID)
            .collection("friends").document(friendID)
            .collection("messages")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("NewMessageViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachMessage = it.toObject(FriendsMessage::class.java)
                        if (eachMessage != null) {
                            messages.add(eachMessage)
                        }
                        messages.sortWith(compareBy { it -> it.date })

                        val message = messages.get(messages.size - 1)
                        holder.textView.message_friends_recycler_last_message.text =
                            message.latestMessage!!
                        holder.textView.message_friends_recycler_time.text = showDate(message.date!!)




                    }

                    messages.sortWith(compareBy { it -> it.date })
                }
            }
    }

    fun showDate(given: Date) : String {
        val dateFormatDay = SimpleDateFormat("EEEE")
        val dateFormatDayOfYear = SimpleDateFormat("dd/MM/yyyy")
        val dateFormatTime = SimpleDateFormat("hh:mma")
        val weekOfYear = SimpleDateFormat("w")


        val today = Calendar.getInstance().time
        val thisWeek = weekOfYear.format(today).toPattern().toString()
        val messageWeek = weekOfYear.format(given).toPattern().toString()

        if (dateFormatDayOfYear.format(today).toPattern().toString().equals(dateFormatDayOfYear.format(given).toPattern().toString())) {
            return dateFormatTime.format(given).toPattern().toString()

        } else if (thisWeek.equals(messageWeek)) {
            return dateFormatDay.format(given).toPattern().toString()
        } else {
            return dateFormatDayOfYear.format(given).toPattern().toString()
        }

    }



}