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
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.orbital.snus.R
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewIndividualModuleBinding
import com.orbital.snus.databinding.ModuleReviewIndividualReviewBinding
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
    var reviewDate: Date? = null

    private lateinit var review: UserReview

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_individual_review, container, false
        )

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

        return binding.root
    }

    fun initialiseViews(review: UserReview) {
        setUserPrivilege(firebaseAuth.currentUser!!.uid == review.userID)
        val dateFormatter1: SimpleDateFormat = SimpleDateFormat("dd MMM yy")

        binding.individualReviewTitle.text = review.title
        binding.individualReviewActual.text = "Actual Grade: " + review.actualGrade
        binding.individualReviewCommitment.text = "Commitment: " + review.commitment
        binding.individualReviewDate.text = dateFormatter1.format(review.date!!).toPattern().toString()
        binding.individualReviewExpected.text = "Expected Grade: " + review.expectedGrade
        binding.individualReviewProf.text = "Professor: " + review.prof
        binding.individualReviewDescription.text = "Description:\n" + review.description
        binding.individualReviewWorkload.text = "Workload: " + review.workload
        binding.individualReviewRatings.text = review.rating.toString() + " out of 5"
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
        dialog.edit_rating.setText(review.rating.toString())
        dialog.edit_date.setText(dateFormatter1.format(review.date!!).toPattern().toString())
        dialog.edit_expected.setText(review.expectedGrade)
        dialog.edit_actual.setText(review.actualGrade)
        dialog.edit_commitment.setText(review.commitment)
        dialog.edit_workload.setText(review.workload)
        dialog.edit_prof.setText(review.prof)
        dialog.edit_description.setText(review.description)

        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.edit_date.setOnClickListener {
            hideKeyboard(it)
            setDate(it as TextView)
        }

        dialog.edit_description.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        dialog.edit_close.setOnClickListener { dialog.dismiss() }
        dialog.edit_confirm.setOnClickListener {
            val title = dialog.edit_title.text.toString()
            val rating = dialog.edit_rating.text.toString()
            val expected = dialog.edit_expected.text.toString()
            val actual = dialog.edit_actual.text.toString()
            val commitment = dialog.edit_commitment.text.toString()
            val workload = dialog.edit_workload.text.toString()
            val professor = dialog.edit_prof.text.toString()
            val description = dialog.edit_description.text.toString()

            if (title == "") {
                Toast.makeText(requireContext(), "Please enter Review title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (reviewDate == null) {
                Toast.makeText(requireContext(), "Please enter Review date", Toast.LENGTH_SHORT)
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
        dialog.edit_rating.isEnabled = boolean
        dialog.edit_date.isEnabled = boolean
        dialog.edit_expected.isEnabled = boolean
        dialog.edit_actual.isEnabled = boolean
        dialog.edit_commitment.isEnabled = boolean
        dialog.edit_workload.isEnabled = boolean
        dialog.edit_prof.isEnabled = boolean
        dialog.edit_description.isEnabled = boolean
    }

    fun setDate (v: TextView) {

        // Calendar and Date variables
        val c = Calendar.getInstance()
        var mYear = c[Calendar.YEAR]
        var mMonth = c[Calendar.MONTH]
        var mDay = c[Calendar.DAY_OF_MONTH]


        var dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY") //\n'hh:mm a")


        // DATEPICKER
        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                c.set(mYear, mMonth, mDay)

                reviewDate = c.time
                v.text = dateFormatter.format(reviewDate!!).toPattern().toString()

            }, mYear, mMonth, mDay
        )

        datePickerDialog.show()
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}