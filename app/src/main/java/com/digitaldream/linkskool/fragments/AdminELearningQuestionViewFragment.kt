package com.digitaldream.linkskool.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.activities.AdminELearningActivity
import com.digitaldream.linkskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.digitaldream.linkskool.utils.FunctionUtils.getDate
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.util.Locale


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningQuestionViewFragment :
    Fragment(R.layout.fragment_admin_e_learning_question_view) {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var durationTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var viewQuestionBtn: Button
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentTxt: TextView
    private lateinit var commentTitleTxt: TextView
    private lateinit var commentInput: TextInputLayout

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private val commentList = mutableListOf<CommentDataModel>()


    // Variables to store data
    private var jsonData: String? = null
    private var taskType: String? = null

    private var title: String? = null
    private var description: String? = null
    private var dueDate: String? = null
    private var duration: String? = null

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

        commentClick()

        updateComment()

        parseJson()
    }

    private fun setUpViews(view: View) {
        view.apply {
            dueDateTxt = findViewById(R.id.questionDueDateTxt)
            titleTxt = findViewById(R.id.questionTitleTxt)
            durationTxt = findViewById(R.id.questionDurationTxt)
            descriptionTxt = findViewById(R.id.descriptionTxt)
            viewQuestionBtn = findViewById(R.id.viewQuestionsButton)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            commentTxt = findViewById(R.id.addCommentTxt)
            commentTitleTxt = findViewById(R.id.commentTitleTxt)
            commentInput = findViewById(R.id.commentEditText)
        }
    }

    private fun viewQuestions() {
        viewQuestionBtn.setOnClickListener {
            startActivity(
                Intent(requireContext(), AdminELearningActivity::class.java)
                    .putExtra("from", "question_test")
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


    private fun commentClick() {
        commentTxt.setOnClickListener {
            it.isVisible = false
            commentInput.isVisible = true

            commentInput.editText?.let { edit -> showSoftInput(requireContext(), edit) }
        }

    }

    private fun sendComment() {
        val message = commentInput.editText?.text.toString().trim()
        val date = formatDate2(getDate())

        if (message.isNotBlank()) {
            val commentDataModel = CommentDataModel("id", "id", "Toochi Dennis", message, date)
            commentList.add(commentDataModel)

            commentInput.isVisible = false
            commentTxt.isVisible = true
            commentTitleTxt.isVisible = true

            commentInput.editText?.let { hideKeyboard(it) }

            commentAdapter.notifyDataSetChanged()
        } else {
            commentInput.error = "Please provide a comment"
        }
    }

    private fun updateComment() {
        commentInput.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendComment()

                return@setOnEditorActionListener true
            }else if (actionId == EditorInfo.IME_ACTION_NONE){
                commentInput.isVisible = false
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private fun hideKeyboard(editText: EditText) {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
        editText.setText("")
    }

    private fun parseJson() {
        with(JSONObject(JSONObject(jsonData!!).getString("e"))) {
            title = getString("title")
            description = getString("description")
            duration = getString("objective")
            dueDate = getString("end_date")
        }

        setTextOnFields()
    }

    private fun setTextOnFields() {
        if (title?.isNotBlank() == true) {
            titleTxt.text = title
        }

        if (description?.isNotBlank() == true) {
            descriptionTxt.text = description
        }

        if (duration?.isNotBlank() == true) {
            val durationString = String.format(Locale.getDefault(),"%s minutes", duration)
            durationTxt.text = durationString
        }

        if (dueDate?.isNotBlank() == true) {
            val date = formatDate2(dueDate!!, "custom1")
            dueDateTxt.text = date
        }
    }

}