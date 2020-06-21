package com.orbital.snus.modules.Forum.Posts.Answers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.ForumComment
import com.orbital.snus.data.ForumPost

class AnswersViewModel(val module:String, val subForum:String, val question: String) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _answers = MutableLiveData<List<ForumComment>>()
    val answers: LiveData<List<ForumComment>>
        get() = _answers

    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess : LiveData<Boolean?>
        get() = _addSuccess

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure : LiveData<Exception?>
        get() = _addFailure

    fun addAnswers(comment: ForumComment) {
        val id = db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(question)
            .collection("comments")
            .document()
            .id

        comment.forumID = id

        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(question)
            .collection("comments")
            .document(id).set(comment)
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

    fun addPostSuccessCompleted() {
        _addSuccess.value = null
    }

    fun addPostFailureCompleted() {
        _addFailure.value = null
    }



    fun loadPosts() {
        val answers = ArrayList<ForumComment>()
        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(question)
            .collection("comments")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("AnswerViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val answer = it.toObject(ForumComment::class.java)
                        if (answer != null) {
                            answers.add(answer)
                        }
                    }
                }
                answers.sortByDescending { it.date }
                answers.reverse()
                _answers.value = answers
            }
    }

    private val _delSuccess = MutableLiveData<Boolean?>()
    val delSuccess : LiveData<Boolean?>
        get() = _delSuccess

    private val _delFailure = MutableLiveData<Exception?>()
    val delFailure : LiveData<Exception?>
        get() = _delFailure

    fun delPostSuccessCompleted() {
        _delSuccess.value = null
    }

    fun delPostFailureCompleted() {
        _delFailure.value = null
    }

    fun deleteComment(ID: String) {
        db.collection("modules")
            .document(module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document(question)
            .collection("comments")
            .document(ID).delete()
            .addOnSuccessListener {
                _delSuccess.value = true
                Log.d("Delete Comment", "DocumentSnapshot successfully deleted!")
            }.addOnFailureListener {
                _delFailure.value = it
                Log.w("Delete Comment", "Error deleting document", it)
            }
    }



}