package com.orbital.snus.modules.Review.MainPageFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleReviewMainPageBinding
import java.util.*


class ReviewMainPageFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var firestore: FirebaseFirestore


    private val mods = ArrayList<String>() // holder to store events and for RecyclerViewAdapter to observe

    // on main page, load out all the user's modules
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleReviewMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_main_page, container, false
        )
        binding.button.setOnClickListener {
            val search = binding.moduleReviewMainPageSearch
            if (search.text.toString().trim() == "") {
                search.setError("Missing Field")
                return@setOnClickListener
            }

            val documentID: DocumentReference = db.collection("modules")
                .document(search.text.toString().trim())

            documentID.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        val bundle = Bundle()
                        bundle.putString("module", search.text.toString().trim())
                        Log.d("ReviewMainPage", "Document exists!")
                        findNavController().navigate(R.id.action_reviewMainPageFragment_to_individualModuleFragment2, bundle)
                        hideKeyboard(it)
                    } else {
                        search.setError("Invalid module name ")
                        Log.d("ReviewMainPage", "Invalid input!")
                    }
                } else {
                    Log.d("ReviewMainPage", "Failed with: ", task.exception)
                }
            }
            return@setOnClickListener

        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



}