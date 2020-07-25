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

        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(post.id!!).set(post)
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

    fun delPostSuccessCompleted() {
        _delSuccess.value = null
    }

    fun delPostFailureCompleted() {
        _delFailure.value = null
    }

    fun loadResolvedPosts() {
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
                            if (event.status == true) {
                                forumPosts.add(event)

                            }
                        }
                    }
                }
                forumPosts.sortByDescending { it.date }
                _posts.value = forumPosts
            }
    }

    fun loadUnresolvedPosts() {
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
                            if (event.status == false) {
                                forumPosts.add(event)

                            }
                        }
                    }
                }
                forumPosts.sortByDescending { it.date }
                _posts.value = forumPosts
            }
    }

    // FOR UPDATING EVENTS

    private val _updateSuccess = MutableLiveData<Boolean?>()
    val updateSuccess : LiveData<Boolean?>
        get() = _updateSuccess

    private val _updateFailure = MutableLiveData<Exception?>()
    val updateFailure : LiveData<Exception?>
        get() = _updateFailure

    fun updatePost(post: ForumPost) {
        val id = post.id!!

        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(id).set(post)
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