package com.orbital.snus.modules.Forum.MainPage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.GsonBuilder
import com.orbital.snus.R
import com.orbital.snus.data.Module
import com.orbital.snus.databinding.ModuleForumMainPageBinding
import com.orbital.snus.modules.ModulesActivity
import okhttp3.*
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class MainPageFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()

    private lateinit var firestore: FirebaseFirestore

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    val factory = MainPageViewModelFactory()
    private lateinit var viewModel: MainPageViewModel
    private val mods = ArrayList<Module>() // holder to store events and for RecyclerViewAdapter to observe

    private lateinit var binding: ModuleForumMainPageBinding

    private lateinit var module: Module

    val fetchResponse = MutableLiveData<Boolean>()

    // on main page, load out all the user's modules
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
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

        viewModel.modules.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<Module>> { dbMods ->
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


        // searching function
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

            // previous database manual searching
            val documentID: DocumentReference = db.collection("modules")
                .document(modifiedSearch)

            documentID.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    System.out.println("task completed")

                    val bundle = Bundle()
                    bundle.putString("module", modifiedSearch)

                    val document = task.result
                    if (document!!.exists()) { // if document null?
                        Log.d("ReviewMainPage", "Document exists!")
                        findNavController().navigate(R.id.action_mainPageFragment_to_individualModuleReviewInformationFragment, bundle)
                    } else {

                        System.out.println("running JSON")
                        fetchJson(modifiedSearch)

                        fetchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            if (it != null) {
                                db.collection("modules").document(module.moduleCode!!.toUpperCase(Locale.ROOT)).set(module)
                                findNavController().navigate(R.id.action_mainPageFragment_to_individualModuleReviewInformationFragment, bundle)
                                fetchComplete()
                            }
                        })
                    }
                } else {
                    Log.d("ReviewMainPage", "Failed with: ", task.exception)
                }
            }
            return@setOnClickListener

        }

        return binding.root
    }

    fun configurePage(boolean: Boolean) {
        binding.forumMainPageRecyclerview.isEnabled = boolean
        binding.button.isEnabled = boolean
        binding.moduleReviewMainPageSearch.isEnabled = boolean
        binding.textView5.isEnabled = boolean
    }

    fun fetchComplete() {
        fetchResponse.value = null
    }

    fun fetchJson(moduleCode: String) {

        configurePage(false)
        // can consider creating a dialog, that shows loading
        // then when the requests are done dismiss the dialog

        val api = "https://api.nusmods.com/v2/"
        val acadYear = "2019-2020"
        val extension = "$acadYear/modules/$moduleCode.json"

        System.out.println("building request")
        val request = Request.Builder().url(api + extension).build()
        val client = OkHttpClient()

        // main UI thread cannot execute HTTP request
        // android doesnt allow
        System.out.println("request built")
        System.out.println("requesting")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@MainPageFragment.activity?.runOnUiThread {
                    binding.moduleReviewMainPageSearch.setError("Invalid module name")
                    configurePage(true)
                }
                System.out.println("failed " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                System.out.println("found")
                val body = response.body?.string()
                System.out.println(body)

                if (body != null && body.contains("html")) {
                    this@MainPageFragment.activity?.runOnUiThread {
                        binding.moduleReviewMainPageSearch.setError("Invalid module name")
                        configurePage(true)
                    }
                } else {
                    val gson = GsonBuilder().create()
                    val mod = gson.fromJson(body, DummyModule::class.java)

                    val convertedMod = convertMod(mod)
                    System.out.println(mod)

                    module = convertedMod

                    this@MainPageFragment.activity?.runOnUiThread {
                        fetchResponse.value = true
                        configurePage(true)
                    }
                }
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(MainPageViewModel::class.java)
        viewModel.loadModules()
        viewModel.modules.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<Module>> { mods ->
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

    fun convertMod(mod: DummyModule) : Module {
        val lessons = ArrayList<String>()

        mod.semesterData?.forEach {
            it.timetable.forEach {
                if (!lessons.contains(it.lessonType)) {
                    lessons.add(it.lessonType)
                }
            }
        }
        System.out.println(lessons)

        // implement forums
        lessons.forEach {
            val test = HashMap<String, String>()
            test.put("Creation", "Confirmation")

            db.collection("modules")
                .document(mod.moduleCode!!)
                .collection("forums")
                .document(it.toUpperCase(Locale.ROOT))
                .set(test as Map<String, Any>)
        }

        return Module(mod.acadYear, mod.preclusion, mod.description, mod.title,
                        mod.department, mod.faculty, mod.workload, mod.prerequisite, mod.moduleCredit, mod.moduleCode,
                        lessons, mod.prereqTree, mod.fulfillRequirements)
    }

}

class DummyModule(val acadYear: String? = null,
             val preclusion: String? = null,
             val description: String? = null,
             val title: String? = null,
             val department: String? = null,
             val faculty: String? = null,
             val workload: List<Int>? = null,
             val prerequisite: String? = null,
             val moduleCredit: String? = null,
             val moduleCode: String? = null,
             val semesterData: List<SemesterData>? = null,
             val prereqTree: Any? = null,
             val fulfillRequirements: List<String>? = null) {

    override fun toString(): String {
        return "acadYear " + acadYear + "\n" +
                "preclusion " + preclusion + "\n" +
                "title " + title + "\n" +
                "department " + department + "\n" +
                "faculty " + faculty + "\n" +
                "workload " + workload + "\n" +
                "prerequisite " + prerequisite + "\n" +
                "moduleCredit " + moduleCredit + "\n" +
                "moduleCode " + moduleCode + "\n" +
                "semesterData " + semesterData + "\n" +
                "prereqTree " + prereqTree + "\n" +
                "fulfillRequirements " + fulfillRequirements + "\n"
    }

}

class SemesterData(val semester: Int, val examDate: String, val examDuration: String,
                        val timetable: List<Lesson>)

class Lesson(val classNo: String, val startTime: String, val endTime: String,
                val weeks: Any, val venue: String, val day: String,
                val lessonType: String, val size: Int)