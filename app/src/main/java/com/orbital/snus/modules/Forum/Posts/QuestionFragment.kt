package com.orbital.snus.modules.Forum.Posts

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.orbital.snus.R
import com.orbital.snus.data.ForumPost
import com.orbital.snus.databinding.ModuleForumQuestionBinding
import com.orbital.snus.modules.ModulesActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.module_forum_individual_module.*
import kotlinx.android.synthetic.main.module_forum_question.*
import kotlinx.android.synthetic.main.module_forum_question_dialog_edit.*
import kotlin.properties.Delegates

class QuestionFragment : Fragment() {
    val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ModuleForumQuestionBinding
    private lateinit var post: ForumPost

    val storage = FirebaseStorage.getInstance().getReference("Forum_Posts")
    private lateinit var imageRef: StorageReference
    var imageUri: Uri? = null
    var downloadUrl: String? =  null

    private lateinit var viewModel: PostViewModel
    private lateinit var factory: PostViewModelFactory

    var userPrivilege: Boolean? = null

    private lateinit var dialog: Dialog

    var toAns: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        binding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_question, container, false)

        post = (requireArguments().get("post") as ForumPost)
        downloadUrl = post.photoURL
        initiateViews()

        val module = requireArguments().get("module") as String
        val subForum = requireArguments().get("subForum") as String
        val question = post.id

        factory = PostViewModelFactory(module, subForum)
        viewModel = ViewModelProvider(this, factory).get(PostViewModel::class.java)

        binding.buttonAnswer.setOnClickListener {
            // go to the answers page
            toAns = true

            val bundle = Bundle()
            bundle.putString("module", module)
            bundle.putString("subForum", subForum)
            bundle.putString("question", question)

            it.findNavController().navigate(R.id.action_questionFragment_to_answersFragment, bundle)
        }

        imageRef = storage.child("${module}/${subForum}")
        imageRef = imageRef.child(post.id!!)

        binding.buttonEdit.setOnClickListener {
            // Edit the page
            dialog = Dialog(requireContext())
            showPopup(it)
        }

        binding.buttonDelete.setOnClickListener {
            // delete post
            configurePage(false)
            imageRef.delete()
            viewModel.deletePost(post.id!!)
            viewModel.delSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully deleted", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.action_questionFragment_to_postsFragment, requireArguments())
                    viewModel.delPostSuccessCompleted()
                }
            })
            viewModel.delFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    configurePage(true)
                    viewModel.delPostFailureCompleted()
                }
            })
        }

        binding.resolvedButton.setOnClickListener {
            post.resolvedPost()
            viewModel.updatePost(post)

            viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post resolved", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updatePostSuccessCompleted()

                    // unlock page
                    configurePage(true)

                    findNavController().navigate(R.id.action_questionFragment_to_postsFragment, requireArguments())
                }
            })
            viewModel.updateFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.updatePostFailureCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)
                }
            })

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (toAns) {
            (activity as ModulesActivity).hideNavBar()
        } else {
            (activity as ModulesActivity).showNavBar()
        }
        toAns = false
    }


    fun initiateViews() {
        // if the user is not the poster, cannot delete or edit
        setUserPrivilege(firebaseAuth.currentUser!!.uid == post.userid)
        binding.textTitle.text = post.title
        binding.textQuestion.text = post.question

        if (post.photoURL != null) {
            Picasso.get().load(post.photoURL).into(binding.attachImageInQuestion)
        }
    }

    // user can see and interact with edit and delete buttons
    fun setUserPrivilege(boolean: Boolean) {
        userPrivilege = boolean
        binding.buttonDelete.isVisible = boolean
        binding.buttonDelete.isEnabled = boolean

        binding.resolvedButton.isVisible = boolean
        binding.resolvedButton.isEnabled = boolean

        binding.buttonEdit.isVisible = boolean
        binding.buttonEdit.isEnabled = boolean


        if (post.status!!) {
            binding.resolvedButton.isVisible = false
            binding.resolvedButton.isEnabled = false
        }
    }

    fun configurePage(boolean: Boolean) {
        binding.buttonAnswer.isEnabled = boolean
        if (userPrivilege == true) {
            binding.buttonEdit.isEnabled = boolean
            binding.buttonDelete.isEnabled = boolean
            binding.resolvedButton.isEnabled = boolean
        }
    }

    fun showPopup(v: View?) {
        dialog.setContentView(R.layout.module_forum_question_dialog_edit)
        dialog.edit_title.setText(binding.textTitle.text)
        dialog.edit_question.setText(binding.textQuestion.text)

        dialog.editImageInQuestion.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, QuestionFragment.PERMISSION_CODE);
                } else {
                    //permission already granted
                    pickImageFromGallery();
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        if (post.photoURL != null) {
            Picasso.get().load(post.photoURL).into(dialog.editImageInQuestion)
        }

        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.edit_close.setOnClickListener { dialog.dismiss() }
        dialog.button_confirm.setOnClickListener {
            if (dialog.edit_title.text.toString() == "" || dialog.edit_question.text.toString() == "") {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            configureDialog(false)
            configurePage(false)

            post.updatePost(dialog.edit_title.text.toString(), dialog.edit_question.text.toString(), downloadUrl)

            viewModel.updatePost(post)

            viewModel.updateSuccess.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), "Post successfully updated", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.updatePostSuccessCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)

                    //updates the field of events fragment
                    initiateViews()
                    dialog.dismiss()
                }
            })
            viewModel.updateFailure.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if (it != null) {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    viewModel.updatePostFailureCompleted()

                    // unlock page
                    configureDialog(true)
                    configurePage(true)
                }
            })

        }
    }

    fun configureDialog(boolean: Boolean) {
        dialog.edit_title.isEnabled = boolean
        dialog.edit_question.isEnabled = boolean
        dialog.edit_close.isEnabled = boolean
        dialog.button_confirm.isEnabled = boolean
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_questionFragment_to_postsFragment, requireArguments())
            }
        })
    }

//    private fun onClickListener(module:String, subForum:String, question: String): View.OnClickListener? {
//        return View.OnClickListener {
//            val bundle = Bundle()
//            bundle.putString("module", module)
//            bundle.putString("subForum", subForum)
//            bundle.putString("question", question)
//
//            it.findNavController().navigate(R.id.action_questionFragment_to_answersFragment, bundle)
//        }
//    }

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

                dialog.editImageInQuestion.setImageURI(imageUri)

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
                        downloadUrl = task.result.toString()
                        val url = downloadUrl.toString()
                        Log.d("DIRECT LINK >>>>>>>>>", url)
                    }
                }


            }
        }
}