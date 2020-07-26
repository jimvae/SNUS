package com.orbital.snus.modules.Review

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewAddReviewBinding
import com.orbital.snus.modules.ModulesActivity
import kotlinx.android.synthetic.main.module_review_dialog_extra_information.*
import kotlinx.android.synthetic.main.profile_main_status_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class AddReviewFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var factory : ReviewDataViewModelFactory
    private lateinit var viewModel: ReviewDataViewModel
    private lateinit var binding: ModuleReviewAddReviewBinding

    var reviewDate: Date? = Calendar.getInstance().time

    val grades = arrayOf("", "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D+", "D", "F", "CS", "CU")
    private lateinit var spinnerExpected: Spinner
    private lateinit var spinnerActual: Spinner

    private lateinit var expected: String
    private lateinit var actual: String

    val commitmentLevel = arrayOf("", "1", "2", "3","4", "5", "6", "7", "8", "9", "10", "11", "12", "13","14", "15", "16", "17", "18", "19", "20")
    private lateinit var spinnerCommit: Spinner
    private lateinit var commitment: String

    val workloadLevels = arrayOf("", "1", "2", "3","4", "5", "6", "7", "8", "9", "10", "11", "12", "13","14", "15", "16", "17", "18", "19", "20")
    private lateinit var workloadSpinner: Spinner
    private lateinit var workload: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_add_review, container, false
        )

//        (activity as ModulesActivity).hideNavBar()

        val moduleName = requireArguments().get("module") as String

        factory = ReviewDataViewModelFactory(moduleName)
        viewModel = ViewModelProvider(this, factory).get(ReviewDataViewModel::class.java)
        spinnerSetup()

        var dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY") //\n'hh:mm a")
        binding.individualReviewAddDate.text = dateFormatter.format(reviewDate!!).toPattern().toString()

        binding.individualReviewAddDescription.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        binding.individualReviewPostReview.setOnClickListener {
            val title = binding.individualReviewAddTitle.text.toString().trim()
            val rating = binding.individualReviewAddRatingBar.rating.toInt().toString().trim()
            val professor = binding.individualReviewAddProf.text.toString().trim()
            val description = binding.individualReviewAddDescription.text.toString().trim()

            if (title == "") {
                Toast.makeText(requireContext(), "Please enter Review title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (rating.toInt() > 5 || rating.toInt() < 0) {
                Toast.makeText(requireContext(), "Please enter rating out of 5", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (expected == "") {
                Toast.makeText(requireContext(), "Please enter expected grade", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (actual == "") {
                Toast.makeText(requireContext(), "Please enter actual grade", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (commitment == "") {
                Toast.makeText(requireContext(), "Please enter commitment", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (workload == "") {
                Toast.makeText(requireContext(), "Please enter workload", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (professor == "") {
                Toast.makeText(requireContext(), "Please enter professor", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (description == "") {
                Toast.makeText(requireContext(), "Please enter description", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //disable page
            configurePage(false)

            val review = UserReview(null,firebaseAuth.currentUser!!.uid,  title, reviewDate, rating.toInt(), expected, actual, commitment, workload, professor, description)
            viewModel.addReview(review)
            viewModel.addSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Review successfully added", Toast.LENGTH_SHORT)
                        .show()
                    val bundle = Bundle()
                    bundle.putString("module", moduleName)
                    findNavController().navigate(R.id.action_addReviewFragment_to_individualModuleFragment2, bundle)
                    viewModel.addEventSuccessCompleted()
                }
            })
            viewModel.addFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.addEventFailureCompleted()
                }
            })
        }

        binding.commitmentInfo.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.module_review_dialog_extra_information)
            dialog.extra_information_title.setText("Commitment")
            dialog.extra_information_details.setText("The actual number of hours you put in this module per week including your timetable and the actual amount of time to study/prepare")
            dialog.extra_information_back.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.workloadInfo.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.module_review_dialog_extra_information)
            dialog.extra_information_title.setText("Workload")
            dialog.extra_information_details.setText("The number of hours you have to spend in this module per week including your timetable and suggested amount of time to study/prepare")
            dialog.extra_information_back.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        return binding.root
    }

    fun configurePage(boolean: Boolean) {
        binding.individualReviewAddTitle.isEnabled = boolean
        binding.individualReviewAddRatingBar.isEnabled = boolean
        binding.individualReviewAddExpectedSpinner.isEnabled = boolean
        binding.individualReviewAddActualSpinner.isEnabled = boolean
        binding.individualReviewAddCommitmentSpinner.isEnabled = boolean
        binding.individualReviewAddWorkloadSpinner.isEnabled = boolean
        binding.individualReviewAddProf.isEnabled = boolean
        binding.individualReviewAddDescription.isEnabled = boolean

        binding.individualReviewPostReview.isEnabled = boolean
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (activity as ModulesActivity).showNavBar()
    }

    fun spinnerSetup() {
        spinnerExpected = binding.individualReviewAddExpectedSpinner
        spinnerExpected.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, grades)

        spinnerExpected.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                expected = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                expected = grades.get(position)
            }

        }

        spinnerActual = binding.individualReviewAddActualSpinner
        spinnerActual.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, grades)

        spinnerActual.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                actual = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                actual = grades.get(position)
            }

        }

        spinnerCommit = binding.individualReviewAddCommitmentSpinner
                spinnerCommit.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, commitmentLevel)

        spinnerCommit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                commitment = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                commitment = commitmentLevel.get(position)
            }

        }

        workloadSpinner = binding.individualReviewAddWorkloadSpinner
        workloadSpinner.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, workloadLevels)

        workloadSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                workload = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                workload = workloadLevels.get(position)
            }

        }
    }
}