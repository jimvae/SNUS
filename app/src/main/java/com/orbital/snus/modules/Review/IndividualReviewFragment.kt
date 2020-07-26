package com.orbital.snus.modules.Review

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewIndividualReviewBinding
import com.orbital.snus.modules.ModulesActivity
import kotlinx.android.synthetic.main.module_forum_question_dialog_edit.*
import kotlinx.android.synthetic.main.module_forum_question_dialog_edit.edit_close
import kotlinx.android.synthetic.main.module_forum_question_dialog_edit.edit_title
import kotlinx.android.synthetic.main.module_review_individual_review_dialog_edit.*
import java.text.SimpleDateFormat
import java.util.*

class IndividualReviewFragment : Fragment() {
    private lateinit var binding: ModuleReviewIndividualReviewBinding
    var userPrivilege: Boolean? = null
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var viewModel: ReviewDataViewModel
    private lateinit var factory: ReviewDataViewModelFactory

    private lateinit var dialog: Dialog
    var reviewDate: Date? = Calendar.getInstance().time // reviewDate will change automatically to today's date

    private lateinit var review: UserReview

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
            inflater, R.layout.module_review_individual_review, container, false
        )

//        (activity as ModulesActivity).hideNavBar()

        review = requireArguments().get("review") as UserReview
        factory = ReviewDataViewModelFactory(requireArguments().get("module") as String)
        viewModel = ViewModelProvider(this, factory).get(ReviewDataViewModel::class.java)
        initialiseViews(review)

        binding.buttonDelete.setOnClickListener {
            configurePage(false)
            viewModel.deleteReview(review.id!!)
            viewModel.delSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Review successfully deleted", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_individualReviewFragment_to_individualModuleFragment2, requireArguments())
                    viewModel.delReviewSuccessCompleted()
                }
            })
            viewModel.delFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.delReviewFailureCompleted()
                }
            })
        }

        binding.buttonEdit.setOnClickListener {
            dialog = Dialog(requireContext())
            showPopup(it)
        }

        // pass in review and module for path
        binding.textQuestions.setOnClickListener {
            findNavController().navigate(R.id.action_individualReviewFragment_to_individualReviewThreadFragment, requireArguments())
        }

        return binding.root
    }

    fun initialiseViews(review: UserReview) {
        setUserPrivilege(firebaseAuth.currentUser!!.uid == review.userID)
        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("dd MMM yy")

        binding.individualReviewTitle.text = review.title
        binding.individualReviewActual.text = review.actualGrade
        binding.individualReviewCommitment.text = review.commitment
        binding.individualReviewDate.text = dateFormatter1.format(review.date!!).toPattern().toString()
        binding.individualReviewExpected.text = review.expectedGrade
        binding.individualReviewProf.text = review.prof
        binding.individualReviewDescription.text = review.description
        binding.individualReviewWorkload.text = review.workload

        binding.individualReviewRatingBar.rating = review.rating!!.toFloat()
        binding.individualReviewRatingBar.setIsIndicator(true)
    }

    // user can see and interact with edit and delete buttons
    fun setUserPrivilege(boolean: Boolean) {
        userPrivilege = boolean
        binding.buttonDelete.isVisible = boolean
        binding.buttonDelete.isEnabled = boolean

        binding.buttonEdit.isVisible = boolean
        binding.buttonEdit.isEnabled = boolean
    }

    fun configurePage(boolean: Boolean) {
        if (userPrivilege == true) {
            binding.buttonEdit.isEnabled = boolean
            binding.buttonDelete.isEnabled = boolean
        }
    }

    fun showPopup(v: View?) {
        dialog.setContentView(R.layout.module_review_individual_review_dialog_edit)

        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("dd MMM yy")
        dialog.edit_title.setText(review.title)
        dialog.edit_ratingbar.rating = review.rating!!.toFloat()
        dialog.edit_date.setText(dateFormatter1.format(reviewDate!!).toPattern().toString())
        dialog.edit_prof.setText(review.prof)
        dialog.edit_description.setText(review.description)

        spinnerExpected = dialog.spinner_expected
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

        spinnerActual = dialog.spinner_actual
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

        spinnerCommit = dialog.spinner_commitment
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

        workloadSpinner = dialog.spinner_workload
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


        spinnerExpected.setSelection(grades.indexOf(review.expectedGrade))
        spinnerActual.setSelection(grades.indexOf(review.actualGrade))
        spinnerCommit.setSelection(commitmentLevel.indexOf(review.commitment))
        workloadSpinner.setSelection(workloadLevels.indexOf(review.workload))


        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.edit_description.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        dialog.edit_close.setOnClickListener { dialog.dismiss() }
        dialog.edit_confirm.setOnClickListener {
            val title = dialog.edit_title.text.toString().trim()
            val rating = dialog.edit_ratingbar.rating.toInt().toString()
            val professor = dialog.edit_prof.text.toString().trim()
            val description = dialog.edit_description.text.toString().trim()

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

            configureDialog(false)
            configurePage(false)

            review.updateReview(title, reviewDate!!, rating.toInt(), expected, actual, commitment, workload, professor, description)
            viewModel.updateReview(review)

            viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Review successfully updated", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updateReviewSuccessCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)

                    //updates the field of events fragment
                    initialiseViews(review)
                    dialog.dismiss()
                }
            })
            viewModel.updateFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.updateReviewFailureCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)
                }
            })

        }
    }

    fun configureDialog(boolean: Boolean) {
        dialog.edit_title.isEnabled = boolean
        dialog.edit_ratingbar.isEnabled = boolean
        dialog.edit_date.isEnabled = boolean
        dialog.spinner_expected.isEnabled = boolean
        dialog.spinner_actual.isEnabled = boolean
        dialog.spinner_commitment.isEnabled = boolean
        dialog.spinner_workload.isEnabled = boolean
        dialog.edit_prof.isEnabled = boolean
        dialog.edit_description.isEnabled = boolean
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        (activity as ModulesActivity).showNavBar()
    }

}