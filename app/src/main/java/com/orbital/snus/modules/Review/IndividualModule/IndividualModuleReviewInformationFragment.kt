package com.orbital.snus.modules.Review.IndividualModule

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbital.snus.R
import com.orbital.snus.data.Module
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ModuleReviewIndividualReviewInformationBinding
import com.orbital.snus.modules.ModulesActivity
import kotlinx.android.synthetic.main.profile_main_status_dialog.*

class IndividualModuleReviewInformationFragment : Fragment() {

    private lateinit var binding: ModuleReviewIndividualReviewInformationBinding

    private var modStatus = MutableLiveData<String>()
    private lateinit var userData: UserData
    private lateinit var module: String
    private lateinit var moduleInformation: String
    private lateinit var moduleTitle: String
    private lateinit var modulePrerequisites: String
    private lateinit var modulePreclusions: String
    private lateinit var departmentFacultyMC: String

    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()

    private var backTrack = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil
            .inflate(layoutInflater, R.layout.module_review_individual_review_information, container, false)

        (requireActivity() as ModulesActivity).hideNavBar()


        // figure out how to access NUSMODS API to extract module information
        // maybe if it works can consider how to include other information
        // like if can SU, prerequisites, content (lab, lect etc)
        // or just load the nusmods page in the app? xd

        module = requireArguments().get("module") as String
        moduleInformation = requireArguments().get("moduleInformation") as String
        moduleTitle = requireArguments().get("title") as String
        modulePreclusions = requireArguments().get("preclusions") as String
        modulePrerequisites = requireArguments().get("prerequisites") as String
        departmentFacultyMC = requireArguments().get("department") as String

        binding.textModuleName2.text = moduleTitle
        binding.textModuleInformation.text = moduleInformation
        binding.textModuleName.text = module
        binding.textModulePreclusions.text = modulePreclusions
        binding.textModulePrerequisites.text = modulePrerequisites
        binding.textViewDepartmentMC.text = departmentFacultyMC


        binding.textGotoReview.setOnClickListener {
            backTrack = false
            findNavController().navigate(R.id.action_individualModuleReviewInformationFragment_to_individualModuleFragment2, requireArguments())
        }

        modStatus.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.textModuleStatus.setText(it)
            }
        })

        getModuleStatus(module)

        binding.textModuleStatus.setOnClickListener {
            when (binding.textModuleStatus.text) {
                "Enroll in Module" -> {
                    // add module to user module list
                    val holder = ArrayList(userData.moduleList!!.toMutableList())
                    holder.add(module)
                    userData.updateModules(holder)

                    db.collection("users").document(userData.userID!!).set(userData)
                        .addOnSuccessListener {
                            binding.textModuleStatus.text = "Module Enrolled"
                        }
                }

                "Module Enrolled" -> {
                    // choose to delete module
                    val dialog = Dialog(requireContext())
                    dialog.setContentView(R.layout.profile_main_status_dialog)
                    dialog.profile_main_status_dialog_title.setText("Delete Module")
                    dialog.profile_main_status_dialog_details.setText("Are you sure you want to delete this module?")
                    dialog.profile_main_status_confirm.setText("Confirm Delete")

                    dialog.profile_main_status_confirm.setOnClickListener {
                        val holder = ArrayList(userData.moduleList!!.toMutableList())
                        holder.remove(module)
                        userData.updateModules(holder)
                        db.collection("users").document(userData.userID!!).set(userData)
                            .addOnSuccessListener {
                                binding.textModuleStatus.text = "Enroll in Module"
                                dialog.dismiss()
                            }
                    }

                    dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
                    dialog.show()
                }
            }
        }

        return binding.root
    }

    fun getModuleStatus(module: String) {

        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .get().addOnSuccessListener {
                if (it.exists()) {
                    userData  = it.toObject(UserData::class.java)!!

                    if (userData.moduleList!!.contains(module)) {
                        modStatus.value = "Module Enrolled"
                    } else {
                        modStatus.value = "Enroll in Module"
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (backTrack) {
            (activity as ModulesActivity).showNavBar()
        } else {
            (activity as ModulesActivity).hideNavBar()
        }
        backTrack = true
    }
}