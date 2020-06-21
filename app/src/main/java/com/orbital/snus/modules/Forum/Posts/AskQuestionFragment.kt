package com.orbital.snus.modules.Forum.Posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.orbital.snus.R
import com.orbital.snus.databinding.ModuleForumAskquestionBinding
import com.orbital.snus.databinding.ModuleForumPostsBinding
import com.orbital.snus.modules.ModulesActivity

class AskQuestionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        (activity as ModulesActivity).hideNavBar()

        val binding: ModuleForumAskquestionBinding = DataBindingUtil.inflate(
            inflater, R.layout.module_forum_askquestion, container, false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as ModulesActivity).showNavBar()
    }
}