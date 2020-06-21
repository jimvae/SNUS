package com.orbital.snus.modules.Forum.IndividualModule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class IndividualModuleViewModel(val moduleName: String): ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _subForums = MutableLiveData<List<String>>()
    val subForums: LiveData<List<String>>
        get() = _subForums

    fun loadForums() {
        val mods = ArrayList<String>()
        db.collection("modules")
            .document(moduleName)
            .collection("forums")
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
                _subForums.value = mods
            }
    }
}