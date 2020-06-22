package com.orbital.snus.modules.Review.IndividualModule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.UserReview

class IndividualModuleReviewViewModel(val moduleName: String): ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _reviewList = MutableLiveData<List<UserReview>>()
    val reviewList: LiveData<List<UserReview>>
        get() = _reviewList

    fun loadReview() {
        val reviews = ArrayList<UserReview>()
        db.collection("modules")
            .document(moduleName)
            .collection("reviews")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("ModuleReviewViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val review = it.toObject(UserReview::class.java)
                        if (review != null) {
                            reviews.add(review)
                        }
                    }
                }
                _reviewList.value = reviews
            }
    }
}