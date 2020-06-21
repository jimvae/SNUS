package com.orbital.snus.modules.Forum.Posts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.ForumPost
import com.orbital.snus.data.UserEvent

class PostViewModel(val module:String, val subForum:String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _posts = MutableLiveData<List<ForumPost>>()
    val posts: LiveData<List<ForumPost>>
        get() = _posts

    // ADD POSTS
    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess : LiveData<Boolean?>
        get() = _addSuccess

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure : LiveData<Exception?>
        get() = _addFailure

    fun addPost(post: ForumPost) {
        val id = db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document().id

        post.id = id

        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(id).set(post)
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

    // FOR DELETING POSTS
    private val _delSuccess = MutableLiveData<Boolean?>()
    val delSuccess : LiveData<Boolean?>
        get() = _delSuccess

    private val _delFailure = MutableLiveData<Exception?>()
    val delFailure : LiveData<Exception?>
        get() = _delFailure

    fun deletePost(ID: String) {
        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(ID).delete()
            .addOnSuccessListener {
                _delSuccess.value = true
                Log.d("Delete Post", "DocumentSnapshot successfully deleted!")
            }.addOnFailureListener {
                _delFailure.value = it
                Log.w("Delete Post", "Error deleting document", it)
            }
    }

    fun delEventSuccessCompleted() {
        _delSuccess.value = null
    }

    fun delEventFailureCompleted() {
        _delFailure.value = null
    }

    fun loadPosts() {
        val forumPosts = ArrayList<ForumPost>()
        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.w("PostViewModel", firebaseFirestoreException.toString())
                return@addSnapshotListener
            }
            if (querySnapshot != null) {
                val documents = querySnapshot.documents
                documents.forEach {
                    val event = it.toObject(ForumPost::class.java)
                    if (event != null) {
                        forumPosts.add(event)
                    }
                }
            }
            forumPosts.sortByDescending { it.date }
            _posts.value = forumPosts
        }
    }

}