package com.orbital.snus.opening

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.orbital.snus.R
import com.orbital.snus.dashboard.DashboardActivity
import com.orbital.snus.data.ForumNameList
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.FragmentOpeningProfileSetupBinding
import com.orbital.snus.profile.EditProfileFragment

class ProfileSetUpFragment : Fragment() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentOpeningProfileSetupBinding
    val storage = FirebaseStorage.getInstance().getReference("Users")
    val userRef = storage.child(firebaseAuth.currentUser!!.uid)
    val imageRef = userRef.child("profile_picture")
    var imageUri: Uri? = null
    var downloadUrl: Uri? =  null

    private lateinit var userData: UserData

    val courses = arrayOf("", "Computer Science", "Business Analytics", "Computer Engineering", "Information Systems", "Information Security")
    private lateinit var spinnerCourse: Spinner
    private lateinit var course: String

    val year = arrayOf("", "1", "2", "3", "4", "5")
    private lateinit var spinnerYear: Spinner
    private lateinit var currYear: String

    private lateinit var forumNameList: ForumNameList
    private val uid = firebaseAuth.currentUser!!.uid


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // If logged in, connect to dashboard


        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_opening_profile_setup, container, false)
        spinnerSetup()
        fetchForumNameList()

        binding.firstLoginConfirm.setOnClickListener {
            val bio = binding.firstLoginBio.text.toString().trim()
            val name = binding.firstLoginName.text.toString().trim()
            val forumName = binding.firstLoginForumName.text.toString().trim()
            val gitHub = binding.firstLoginGithub.text.toString().trim()
            val instagram = binding.firstLoginInstagram.text.toString().trim()
            val linkedIn = binding.firstLoginLinkedin.text.toString().trim()
            val faculty = "Computing"
//            val profilePhoto = binding.profilePhoto

            if (TextUtils.isEmpty(name)) {
                binding.firstLoginName.setError("Name is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(forumName)) {
                binding.firstLoginForumName.setError("Forum Name is required")
                return@setOnClickListener
            }

            if (forumNameList.checkIfForumNameIsTaken(forumName)) {
                binding.firstLoginForumName.setError("Forum Name is already taken")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(bio)) {
                binding.firstLoginBio.setError("Bio is required")
                return@setOnClickListener
            }

            if (course == "") {
                Toast.makeText(requireContext(), "Please select your course", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (currYear == "") {
                Toast.makeText(requireContext(), "Please select your year", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            configurePage(false)

            val user = firebaseAuth.currentUser!!

            forumNameList.updateForumName(forumName, firebaseAuth.currentUser!!.uid)

            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener {
                    updateForumNameList(forumNameList)

                    userData = it.toObject((UserData::class.java))!!
                    userData.updateUserData(name, faculty, course, currYear.toInt(), bio, linkedIn, instagram, gitHub, false, downloadUrl.toString(), ArrayList(), forumName)
                    updateUser(userData)
                    startActivity(Intent(activity?.applicationContext, DashboardActivity::class.java))
                    activity?.finish()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Missing User Data: " + it.message, Toast.LENGTH_SHORT).show()
                }
            configurePage(true)

        }

        binding.firstLoginSelectProfilePhoto.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, ProfileSetUpFragment.PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }
        return binding.root
    }

    fun updateForumNameList(list: ForumNameList) {
        db.collection("forumNameList").document("forumNameList").set(list)
            .addOnSuccessListener {
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Unable to update: " + it.message, Toast.LENGTH_SHORT).show()
            }
    }
    private fun spinnerSetup() {
        Toast.makeText(requireContext(), "SetUp", Toast.LENGTH_SHORT).show()
        spinnerCourse = binding.firstLoginCourseSpinner
        spinnerCourse.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, courses)

        spinnerCourse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                course = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                course = courses.get(position)

            }

        }

        spinnerYear = binding.firstLoginYearOfStudySpinner
        spinnerYear.adapter = ArrayAdapter(requireContext(), R.layout.module_review_spinner_layout, year)

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                currYear = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currYear = year.get(position)
            }

        }
    }

    private fun configurePage(boolean: Boolean) {
        binding.firstLoginBio.isEnabled = boolean
        binding.firstLoginName.isEnabled = boolean
        binding.firstLoginConfirm.isEnabled = boolean
        binding.firstLoginCourseSpinner.isEnabled = boolean
        binding.firstLoginGithub.isEnabled = boolean
        binding.firstLoginInstagram.isEnabled = boolean
        binding.firstLoginLinkedin.isEnabled = boolean
//        binding.profilePhoto.isEnabled = boolean
        binding.firstLoginYearOfStudySpinner.isEnabled = boolean
    }

    private fun updateUser(userData1: UserData) {
        val user = firebaseAuth.currentUser!!
        db.collection("users") // users collection
            .document(user.uid) // current userId
            .set(userData1)

    }

    //Uploading image
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchForumNameList() {
        db.collection("forumNameList")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.w("ProfileSetUpFragment", firebaseFirestoreException.toString())
                    return@addSnapshotListener
                }
                if (querySnapshot != null) {
                    val documents = querySnapshot.documents
                    documents.forEach {
                        val forumNameList1 = it.toObject(ForumNameList::class.java)
                        if (forumNameList1 != null) {
                            forumNameList = forumNameList1
                            Log.d("ModuleViewModel", it.id)
                            Log.d("ModuleViewModel", forumNameList1.map.toString())

                        }
                    }
                }
            }

    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            imageUri = data?.data
            Log.d("DIRECT LINK >>>>>>>>>", imageUri!!.toString())

            binding.profilePhoto.setImageURI(imageUri)

            var uploadTask: StorageTask<*>
            uploadTask =  imageRef!!.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation imageRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    downloadUrl = task.result
                    val url = downloadUrl.toString()
                    Log.d("DIRECT LINK >>>>>>>>>", url)
                }
            }


        }
    }

}