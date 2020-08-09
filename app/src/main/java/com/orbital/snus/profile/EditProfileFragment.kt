package com.orbital.snus.profile

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build

import android.os.Build.*
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
import com.orbital.snus.data.TimeLinePost
import com.orbital.snus.data.UserData
import com.orbital.snus.databinding.ProfileMainTimelineEditBinding
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().getReference("Users")
    val userRef = storage.child(firebaseAuth.currentUser!!.uid)
    val imageRef = userRef.child("profile_picture")
    var imageUri: Uri? = null
    var downloadUrl: Uri? =  null

    private lateinit var binding: ProfileMainTimelineEditBinding

    val courses = arrayOf("", "Computer Science", "Business Analytics", "Computer Engineering", "Information Systems", "Information Security")
    private lateinit var spinnerCourse: Spinner
    private lateinit var course: String

    val year = arrayOf("", "1", "2", "3", "4", "5")
    private lateinit var spinnerYear: Spinner
    private lateinit var currYear: String

    private lateinit var forumNameList: ForumNameList
    private val uid = firebaseAuth.currentUser!!.uid

    private lateinit var userData: UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as ProfileActivity).hideNavBar()

        binding = DataBindingUtil.inflate<ProfileMainTimelineEditBinding>(
            inflater, R.layout.profile_main_timeline_edit, container, false)
        spinnerSetup()

        userData = requireArguments().getParcelable<UserData>("userdata") as UserData
        fetchForumNameList()

        // setup page
        binding.editProfileName.setText(userData.fullname)
        binding.editProfileBio.setText(userData.bio)
        binding.editProfileInstagram.setText(userData.insta)
        binding.editProfileGithub.setText(userData.git)
        binding.editProfileLinkedin.setText(userData.linkedIn)
        binding.editProfileCourseSpinner.setSelection(courses.indexOf(userData.course))
        binding.editProfileYearOfStudySpinner.setSelection(year.indexOf(userData.year.toString()))
        binding.editProfileForumName.setText(userData.forumName)
        if (userData.picUri != null) {
            Picasso.get().load(userData.picUri).into(binding.profilePhoto)
        }

        course = userData.course!!
        currYear = userData.year.toString()

        binding.editProfileConfirm.setOnClickListener {
            val bio = binding.editProfileBio.text.toString().trim()
            val name = binding.editProfileName.text.toString().trim()
            val forumName = binding.editProfileForumName.text.toString().trim()
            val gitHub = binding.editProfileGithub.text.toString().trim()
            val instagram = binding.editProfileInstagram.text.toString().trim()
            val linkedIn = binding.editProfileLinkedin.text.toString().trim()
            val faculty = "Computing"

            if (TextUtils.isEmpty(name)) {
                binding.editProfileName.setError("Name is required")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(forumName)) {
                binding.editProfileForumName.setError("Forum Name is required")
                return@setOnClickListener
            }

            if (forumNameList.checkIfForumNameIsTaken(forumName) && !forumNameList.map!!.get(uid).equals(forumName)) {
                binding.editProfileForumName.setError("Forum Name is already taken")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(bio)) {
                binding.editProfileBio.setError("Bio is required")
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

//            val map: HashMap<String, String> = HashMap<String,String>()
//            val list = ForumNameList(map)
//            list.updateForumName(forumName, firebaseAuth.currentUser!!.uid)
//
//            db.collection("forumNameList").document("forumNameList").set(list)
//                .addOnSuccessListener {
//            }.addOnFailureListener {
//                Toast.makeText(requireContext(), "Unable to update: " + it.message, Toast.LENGTH_SHORT).show()
//            }


            userData.updateUserData(name, faculty, course, currYear.toInt(), bio, linkedIn, instagram, gitHub, false, userData.picUri, userData.moduleList,  forumName)
            forumNameList.updateForumName(forumName, firebaseAuth.currentUser!!.uid)



            db.collection("users") // users collection
                .document(userData.userID!!) // current userId
                .set(userData)
                .addOnSuccessListener {
                    updateForumNameList(forumNameList)
                    findNavController().navigate(R.id.action_editProfileFragment2_to_mainTimelineFragment2)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Unable to update: " + it.message, Toast.LENGTH_SHORT).show()
                }
            configurePage(true)
        }

        binding.editProfileSelectProfilePhoto.setOnClickListener {
            //check runtime permission
            if (VERSION.SDK_INT >= VERSION_CODES.M){
                if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
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

    private fun fetchForumNameList() {
        db.collection("forumNameList")
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                Log.w("EditProfileFragment", firebaseFirestoreException.toString())
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

    fun updateForumNameList(list: ForumNameList) {
        db.collection("forumNameList").document("forumNameList").set(list)
            .addOnSuccessListener {
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Unable to update: " + it.message, Toast.LENGTH_SHORT).show()
            }
    }



    private fun configurePage(boolean: Boolean) {
        binding.editProfileBio.isEnabled = boolean
        binding.editProfileName.isEnabled = boolean
        binding.editProfileConfirm.isEnabled = boolean
        binding.editProfileCourseSpinner.isEnabled = boolean
        binding.editProfileGithub.isEnabled = boolean
        binding.editProfileInstagram.isEnabled = boolean
        binding.editProfileLinkedin.isEnabled = boolean
        binding.profilePhoto.isEnabled = boolean
        binding.editProfileYearOfStudySpinner.isEnabled = boolean
    }

    private fun spinnerSetup() {
        spinnerCourse = binding.editProfileCourseSpinner
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

        spinnerYear = binding.editProfileYearOfStudySpinner
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

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ProfileActivity).showNavBar()
    }


    //Uploading image
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        val PERMISSION_CODE = 1001;
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

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            imageUri = data.data
            Log.d("DIRECT LINK >>>>>>>>>", imageUri!!.toString())

            binding.profilePhoto.setImageURI(imageUri)

            var uploadTask: StorageTask<*>
            uploadTask =  imageRef.putFile(imageUri!!)
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
                    userData.picUri = downloadUrl.toString()
                    Log.d("DIRECT LINK >>>>>>>>>", userData.picUri!!)
                }
            }


        }
    }
}