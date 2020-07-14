package com.orbital.snus.modules.Forum.Posts

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumAskquestionBinding
import com.orbital.snus.databinding.ModuleForumPostsBinding
import com.orbital.snus.modules.ModulesActivity
import com.orbital.snus.profile.EditProfileFragment
import java.util.*

class AskQuestionFragment : Fragment() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().getReference("Forum_Posts")
    private lateinit var imageRef: StorageReference
    var imageUri: Uri? = null
    var downloadUrl: Uri? =  null

    private lateinit var factory: PostViewModelFactory
    private lateinit var viewModel: PostViewModel

    private lateinit var binding: ModuleForumAskquestionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_askquestion, container, false)

        val moduleName = (requireArguments().get("module") as String)
        val subForum = (requireArguments().get("subForum") as String)

        factory = PostViewModelFactory(moduleName, subForum)
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

        binding.editQuestion.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        //image name
        val id = db.collection("modules")
            .document(viewModel.module)
            .collection("forums")
            .document(subForum)
            .collection("posts")
            .document().id

        imageRef = storage.child("${moduleName}/${subForum}")
        imageRef = imageRef.child(id)

        binding.attachImage.setOnClickListener {
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
                    requestPermissions(permissions, AskQuestionFragment.PERMISSION_CODE);
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


        binding.buttonConfirm.setOnClickListener {
            hideKeyboard(it)
            val title = binding.editTitle.text.toString()
            val question = binding.editQuestion.text.toString()

            if (title == "") {
                Toast.makeText(requireContext(), "Please enter title", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (question == "") {
                Toast.makeText(requireContext(), "Please enter question", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            configurePage(false)


            val calendar = Calendar.getInstance()
            val post: ForumPost = ForumPost(firebaseAuth.currentUser!!.uid, id,  title,
                calendar.time, false, question, downloadUrl.toString())
            viewModel.addPost(post)

            viewModel.addSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully added", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_askQuestionFragment_to_postsFragment, requireArguments())
                    viewModel.addPostSuccessCompleted()
                }
            })
            viewModel.addFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.addPostFailureCompleted()
                }
            })
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ModulesActivity).showNavBar()
    }

    fun configurePage(boolean: Boolean) {
        binding.editTitle.isEnabled = boolean
        binding.editQuestion.isEnabled = boolean
        binding.buttonConfirm.isEnabled = boolean
    }

    fun hideKeyboard(view: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_askQuestionFragment_to_postsFragment, requireArguments())
            }
        })
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

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            imageUri = data?.data
            Log.d("DIRECT LINK >>>>>>>>>", imageUri!!.toString())

            binding.attachImage.setImageURI(imageUri)

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