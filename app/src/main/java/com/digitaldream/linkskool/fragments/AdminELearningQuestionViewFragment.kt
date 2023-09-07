package com.digitaldream.linkskool.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.ELearningActivity
import com.digitaldream.linkskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.linkskool.models.CommentModel
import com.google.android.material.textfield.TextInputLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionViewFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_view) {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var viewQuestionBtn: Button
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentTxt: TextView
    private lateinit var commentInput: TextInputLayout

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private val commentList = mutableListOf<CommentModel>()


    // Variables to store data
    private var jsonData: String? = null
    private var taskType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(jsonData: String, taskType: String) =
            AdminELearningQuestionViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, jsonData)
                    putString(ARG_PARAM2, taskType)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        viewQuestions()

        setUpCommentRecyclerView()
    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.questionDueDateTxt)
            titleTxt = findViewById(R.id.questionTitleTxt)
            durationTxt = findViewById(R.id.questionDurationTxt)
            viewQuestionBtn = findViewById(R.id.viewQuestionsButton)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            commentTxt = findViewById(R.id.addCommentTxt)
            commentInput = findViewById(R.id.commentEditText)
        }
    }

    private fun viewQuestions() {
        viewQuestionBtn.setOnClickListener {
            startActivity(
                Intent(requireContext(), ELearningActivity::class.java)
                    .putExtra("from", "view_questions")
                    .putExtra("json", jsonData)
            )
        }
    }


    private fun setUpCommentRecyclerView() {
        commentAdapter = AdminELearningCommentAdapter(commentList)

        commentRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun sendComment() {
        commentTxt.setOnClickListener {
            it.isVisible = false
            commentInput.isVisible = true
        }
    }

    private fun updateComment(){

    }

}