package com.orbital.snus.modules.Forum.MainPage

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleForumMainPageBinding
import com.orbital.snus.modules.ModulesActivity
import java.util.*

class MainPageFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var firestore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    val factory = MainPageViewModelFactory()
    private lateinit var viewModel: MainPageViewModel
    private val mods = ArrayList<String>() // holder to store events and for RecyclerViewAdapter to observe

    // on main page, load out all the user's modules
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val binding: ModuleForumMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_main_page, container, false
        )
        firestore = FirebaseFirestore.getInstance()

        viewModel = ViewModelProvider(this, factory).get(MainPageViewModel::class.java)

        viewAdapter = MainPageAdapter(mods)
        viewManager = LinearLayoutManager(activity)

        recyclerView = binding.forumMainPageRecyclerview.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.modules.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<String>> { dbMods ->
            mods.removeAll(mods)
            mods.addAll(dbMods)
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        binding.moduleReviewMainPageSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
                (requireActivity() as ModulesActivity).showNavBar()
            } else {
                (requireActivity() as ModulesActivity).hideNavBar()
            }
        }


        binding.button.setOnClickListener {
            binding.moduleReviewMainPageSearch.clearFocus()
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
                        findNavController().navigate(R.id.action_mainPageFragment_to_individualModuleFragment2, bundle)
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
        viewModel = ViewModelProvider(this, factory).get(MainPageViewModel::class.java)
        viewModel.loadModules()
        viewModel.modules.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<String>> { mods ->
            if (mods.size != 0) {
                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}