package com.orbital.snus.messages.Messaging

import android.content.Context
import com.orbital.snus.databinding.MessagesMessagingBinding
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.Friends
import com.orbital.snus.data.FriendsMessage
import com.orbital.snus.data.UserData
import com.orbital.snus.messages.MessagesActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.messages_messaging_from_recycler.view.*
import kotlinx.android.synthetic.main.messages_messaging_to_recycler.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessagingFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()

    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var binding: MessagesMessagingBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        (activity as MessagesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.messages_messaging, container, false
        )

        val userData = requireArguments().get("userdata") as UserData

        binding.messagesMessagingName.text = userData.fullname
        Picasso.get().load(userData.picUri).into(binding.messagesFriendsRecyclerPhotoActual)

        fetchMessages(userData)

        binding.messagesMessagingRecyclerView.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(this.context)
        }

        binding.messagesMessagingMessageSend.setOnClickListener {

            if (TextUtils.isEmpty(binding.messagesMessagingMessageHere.text.toString())) {
                return@setOnClickListener
            }

            val text = binding.messagesMessagingMessageHere.text.toString()
            val myID = FirebaseAuth.getInstance().currentUser!!.uid
            val friendID = userData.userID!!
            val calendar = Calendar.getInstance()
            val date = calendar.time
            // add to my database
            // add to friend database


            val textID = db.collection("users").document(myID)
                .collection("friends").document(friendID)
                .collection("messages").document().id

            val message = FriendsMessage(textID, myID, friendID, text, date)

            db.collection("users").document(myID)
                .collection("friends").document(friendID)
                .collection("messages").document(textID).set(message)

            db.collection("users").document(friendID)
                .collection("friends").document(myID)
                .collection("messages").document(textID).set(message)

            binding.messagesMessagingMessageHere.setText("")
            hideKeyboard(it)

            binding.messagesMessagingRecyclerView.scrollToPosition(groupAdapter.itemCount - 1)
        }

        return binding.root
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    val messages = ArrayList<FriendsMessage>()
    fun fetchMessages(userData: UserData){
        val myID = FirebaseAuth.getInstance().currentUser!!.uid
        val friendID = userData.userID!!
        db.collection("users").document(myID)
            .collection("friends").document(friendID)
            .collection("messages").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("NewMessageViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    groupAdapter.clear()
                    val messages = ArrayList<FriendsMessage>()
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val eachMessage = it.toObject(FriendsMessage::class.java)
                        if (eachMessage != null) {
                            messages.add(eachMessage)
                        }
                    }

                    messages.sortWith(compareBy { it -> it.date })
                    messages.forEach { eachMessage ->
                        if (eachMessage.sender!!.equals(myID)) {
                            groupAdapter.add(MessageTo(eachMessage))
                        } else {
                            groupAdapter.add(MessageFrom(eachMessage))
                        }
                    }
                    binding.messagesMessagingRecyclerView.scrollToPosition(groupAdapter.itemCount - 1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MessagesActivity).showNavBar()
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
class MessageTo(val message: FriendsMessage) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.messages_messaging_to_recycler
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messages_messaging_recycler_message_to.text = message.latestMessage.toString()
            viewHolder.itemView.messages_messaging_recycler_to_date.text = showDate(message.date!!)

    }

}

class MessageFrom(val message: FriendsMessage) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.messages_messaging_from_recycler
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.messages_messaging_recycler_message_from.text = message.latestMessage.toString()
        viewHolder.itemView.messages_messaging_from_date.text =
            showDate(message.date!!)


    }

}