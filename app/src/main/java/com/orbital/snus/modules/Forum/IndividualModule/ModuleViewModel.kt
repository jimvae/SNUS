package com.orbital.snus.modules.Forum.IndividualModule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ModuleViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _forums = MutableLiveData<List<String>>()
    val forums: LiveData<List<String>>
        get() = _forums

    fun loadForums() {
        val mods = ArrayList<String>()
        db.collection("modules")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("ModuleViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        mods.add(it.id)
                        Log.d("ModuleViewModel", it.id)
                    }
                }
                _forums.value = mods
            }
    }
}