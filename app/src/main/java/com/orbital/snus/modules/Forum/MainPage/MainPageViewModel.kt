package com.orbital.snus.modules.Forum.MainPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.UserEvent

class MainPageViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _modules = MutableLiveData<List<String>>()
    val modules: LiveData<List<String>>
        get() = _modules

    fun loadModules() {
        val mods = ArrayList<String>()
        db.collection("modules")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("ModuleMainPageViewModel", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        mods.add(it.id)
                        Log.d("MainPageViewModel", it.id)
                    }

                }
                _modules.value = mods
            }
    }
}