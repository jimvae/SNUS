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

    // on main page, load out all the user's modules
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleReviewMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_main_page, container, false
        )
        binding.button.setOnClickListener {
            hideKeyboard(it)
            val search = binding.moduleReviewMainPageSearch

            // set all string to uppercase, so the search is consistent
            val modifiedSearch = search.text.toString().toUpperCase(Locale.ROOT).trim()
            if (modifiedSearch == "") {
                search.setError("Missing Field")
                return@setOnClickListener
            }

            val documentID: DocumentReference = db.collection("modules")
                .document(modifiedSearch)

            documentID.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) { // if document null?
                        val bundle = Bundle()
                        bundle.putString("module", modifiedSearch)
                        Log.d("ReviewMainPage", "Document exists!")
                        findNavController().navigate(R.id.action_reviewMainPageFragment_to_individualModuleFragment2, bundle)
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

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



}