package com.orbital.snus.modules.Review


import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.UserEvent
import com.orbital.snus.data.UserReview

class ReviewDataViewModel(val module: String) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // FOR ADDING MODULES
    private val _addSuccess = MutableLiveData<Boolean?>()
    val addSuccess : LiveData<Boolean?>
        get() = _addSuccess

    private val _addFailure = MutableLiveData<Exception?>()
    val addFailure : LiveData<Exception?>
        get() = _addFailure

    fun addReview(review: UserReview) {
        // start to add inside database
        val id = db.collection("modules") // modules collection
            .document(module) // current userId
            .collection("reviews") // user events collection
            .document().id // event document with auto-generated key

        review.id = id

        db.collection("modules") // users collection
            .document(module) // current userId
            .collection("reviews") // user events collection
            .document(id).set(review)
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

    // FOR DELETING REVIEWS
    private val _delSuccess = MutableLiveData<Boolean?>()
    val delSuccess : LiveData<Boolean?>
        get() = _delSuccess

    private val _delFailure = MutableLiveData<Exception?>()
    val delFailure : LiveData<Exception?>
        get() = _delFailure

    fun deleteReview(ID: String) {
        db.collection("modules") // users collection
            .document(module) // current userId
            .collection("reviews") // user events collection
            .document(ID).delete()
            .addOnSuccessListener {
                _delSuccess.value = true
                Log.d("Delete Review", "DocumentSnapshot successfully deleted!")
            }.addOnFailureListener {
                _delFailure.value = it
                Log.w("Delete Review", "Error deleting document", it)
            }
    }

    fun delReviewSuccessCompleted() {
        _delSuccess.value = null
    }

    fun delReviewFailureCompleted() {
        _delFailure.value = null
    }


    // FOR UPDATING REVIEWS

    private val _updateSuccess = MutableLiveData<Boolean?>()
    val updateSuccess : LiveData<Boolean?>
        get() = _updateSuccess

    private val _updateFailure = MutableLiveData<Exception?>()
    val updateFailure : LiveData<Exception?>
        get() = _updateFailure

    fun updateReview(review: UserReview) {
        val id = review.id!!

        db.collection("modules") // users collection
            .document(module) // current userId
            .collection("reviews") // user events collection
            .document(id).set(review)
            .addOnSuccessListener {
                _updateSuccess.value = true
            }.addOnFailureListener {
                _updateFailure.value = it
            }
    }

    fun updateReviewSuccessCompleted() {
        _updateSuccess.value = null
    }

    fun updateReviewFailureCompleted() {
        _updateFailure.value = null
    }
}