package com.digitaldream.linkskool.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningAssignmentInstructionsFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment_instructions) {

    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var gradeTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentTxt: TextView
    private lateinit var addCommentInput: TextInputLayout

    private var json: String? = null
    private var from: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            json = it.getString(ARG_PARAM1)
            from = it.getString(ARG_PARAM2)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminELearningAssignmentInstructionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.assignmentDueDateTxt)
            titleTxt = findViewById(R.id.assignmentTitleTxt)
            gradeTxt = findViewById(R.id.assignmentGradeTxt)
            descriptionTxt = findViewById(R.id.assignmentDescriptionTxt)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            addCommentTxt = findViewById(R.id.addCommentTxt)
            addCommentInput = findViewById(R.id.commentEditText)
        }
    }

    private fun parseJson(jsonObject: String){

    }
}