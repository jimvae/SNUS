package com.orbital.snus.modules.Review

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.dashboard.DashboardDataViewModel
import com.orbital.snus.dashboard.DashboardDataViewModelFactory
import com.orbital.snus.data.UserReview
import com.orbital.snus.databinding.ModuleReviewAddReviewBinding
import com.orbital.snus.databinding.ModuleReviewIndividualModuleBinding
import com.orbital.snus.modules.ModulesActivity
import java.text.SimpleDateFormat
import java.util.*

class AddReviewFragment : Fragment() {

    private lateinit var factory : ReviewDataViewModelFactory
    private lateinit var viewModel: ReviewDataViewModel
    private lateinit var binding: ModuleReviewAddReviewBinding

    var reviewDate: Date? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_review_add_review, container, false
        )

        (activity as ModulesActivity).hideNavBar()

        val moduleName = requireArguments().get("module") as String

        factory = ReviewDataViewModelFactory(moduleName)
        viewModel = ViewModelProvider(this, factory).get(ReviewDataViewModel::class.java)

        binding.individualReviewAddDate.setOnClickListener {
            hideKeyboard(it)
            setDate(it as TextView)
        }

        binding.individualReviewAddDescription.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        binding.individualReviewPostReview.setOnClickListener {
            val title = binding.individualReviewAddTitle.text.toString()
            val rating = binding.individualReviewAddRatings.text.toString()
            val expected = binding.individualReviewAddExpected.text.toString()
            val actual = binding.individualReviewAddActual.text.toString()
            val commitment = binding.individualReviewAddCommitment.text.toString()
            val workload = binding.individualReviewAddWorkload.text.toString()
            val professor = binding.individualReviewAddProf.text.toString()
            val description = binding.individualReviewAddDescription.text.toString()

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

            //disable page
            configurePage(false)

            val review = UserReview(null, title, reviewDate, rating.toInt(), expected, actual, commitment, workload, professor, description)
            viewModel.addModule(review)
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

        return binding.root
    }

    fun configurePage(boolean: Boolean) {
        binding.individualReviewAddTitle.isEnabled = boolean
        binding.individualReviewAddDate.isEnabled = boolean
        binding.individualReviewAddRatings.isEnabled = boolean
        binding.individualReviewAddExpected.isEnabled = boolean
        binding.individualReviewAddActual.isEnabled = boolean
        binding.individualReviewAddCommitment.isEnabled = boolean
        binding.individualReviewAddWorkload.isEnabled = boolean
        binding.individualReviewAddProf.isEnabled = boolean
        binding.individualReviewAddDescription.isEnabled = boolean
    }

    fun setDate (v: TextView) {

        // Calendar and Date variables
        val c = Calendar.getInstance()
        var mYear = c[Calendar.YEAR]
        var mMonth = c[Calendar.MONTH]
        var mDay = c[Calendar.DAY_OF_MONTH]


        var dateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM YYYY'\n'hh:mm a")


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