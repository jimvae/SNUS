package com.orbital.snus.modules.Forum.MainPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.data.Module
import com.orbital.snus.data.UserData
import com.orbital.snus.data.UserEvent

class MainPageViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _modules = MutableLiveData<List<Module>>()
    val modules: LiveData<List<Module>>
        get() = _modules

    fun loadModules() {
        val mods = ArrayList<Module>()

        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .get().addOnSuccessListener {
                if (it.exists()) {
                    val user  = it.toObject(UserData::class.java)!!

                    // check user list for the modules
                    db.collection("modules")
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            if (firebaseFirestoreException != null) {
                                Log.w("ModuleMainPageViewModel", firebaseFirestoreException.toString())
                                return@addSnapshotListener
                            }
                            if (querySnapshot != null) {
                                val documents = querySnapshot.documents
                                documents.forEach {
                                    val mod = it.toObject(Module::class.java)!!

                                    if (user.moduleList!!.contains(mod.moduleCode)) {
                                        mods.add(mod)
                                    }
                                }

                            }
                            _modules.value = mods
                        }

                }
            }

    }
}