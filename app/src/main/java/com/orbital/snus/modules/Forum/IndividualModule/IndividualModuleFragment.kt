package com.orbital.snus.modules.Forum.IndividualModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleForumIndividualModuleBinding
import com.orbital.snus.modules.Forum.MainPage.MainPageAdapter
import com.orbital.snus.modules.Forum.MainPage.MainPageViewModel
import java.util.ArrayList

class IndividualModuleFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var factory: IndividualModuleViewModelFactory

    private lateinit var viewModel: IndividualModuleViewModel
    private val subForums = ArrayList<String>() // holder to store events and for RecyclerViewAdapter to observe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val moduleName: String = (requireArguments().get("module") as String)


        factory = IndividualModuleViewModelFactory(moduleName)

        val binding: ModuleForumIndividualModuleBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_individual_module, container, false
        )

        binding.moduleForumIndividualModuleTitle.text = moduleName + " Forums"


        viewModel = ViewModelProvider(this, factory).get(IndividualModuleViewModel::class.java)

        viewAdapter = IndividualModuleAdapter(moduleName, subForums)
        viewManager = LinearLayoutManager(activity)

        recyclerView = binding.forumIndividualModuleRecyclerview.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewModel.subForums.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<String>> { dbSubForums ->
            subForums.removeAll(subForums)
            subForums.addAll(dbSubForums)
            recyclerView.adapter!!.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        factory = IndividualModuleViewModelFactory(requireArguments().get("module") as String)
        viewModel = ViewModelProvider(this, factory).get(IndividualModuleViewModel::class.java)
        viewModel.loadForums()
        viewModel.subForums.observe(viewLifecycleOwner, androidx.lifecycle.Observer<List<String>> { mods ->
            if (mods.size != 0) {
//                Toast.makeText(requireContext(), "Success retrieval", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(requireContext(), "Failed retrieval", Toast.LENGTH_SHORT).show()
            }
        })
    }

}