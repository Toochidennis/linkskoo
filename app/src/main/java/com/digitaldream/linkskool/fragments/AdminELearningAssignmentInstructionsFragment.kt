package com.digitaldream.linkskool.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.linkskool.adapters.AdminELearningFilesAdapter
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.utils.FileViewModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.FunctionUtils.formatDate2
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AdminELearningAssignmentInstructionsFragment :
    Fragment(R.layout.fragment_admin_e_learning_assignment_instructions) {

    // Define UI elements
    private lateinit var dueDateTxt: TextView
    private lateinit var titleTxt: TextView
    private lateinit var gradeTxt: TextView
    private lateinit var descriptionTxt: TextView
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentTxt: TextView
    private lateinit var commentTitleTxt: TextView
    private lateinit var commentInput: TextInputLayout

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private lateinit var filesAdapter: AdminELearningFilesAdapter
    private val commentList = mutableListOf<CommentDataModel>()
    private var fileList = mutableListOf<AttachmentModel>()

    private lateinit var fileViewModel: FileViewModel

    // Variables to store data
    private var jsonData: String? = null
    private var taskType: String? = null

    private var title: String? = null
    private var dueDate: String? = null
    private var description: String? = null
    private var grade: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonData = it.getString(ARG_PARAM1)
            taskType = it.getString(ARG_PARAM2)
        }

        fileViewModel = ViewModelProvider(this)[FileViewModel::class.java]
    }


    companion object {

        @JvmStatic
        fun newInstance(jsonData: String, taskType: String = "") =
            AdminELearningAssignmentInstructionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, jsonData)
                    putString(ARG_PARAM2, taskType)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        parseJsonObject()

        setUpCommentRecyclerView()

        commentClick()

        updateComment()
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
            commentTxt = findViewById(R.id.addCommentTxt)
            commentTitleTxt = findViewById(R.id.commentTitleTxt)
            commentInput = findViewById(R.id.commentEditText)
        }
    }

    private fun setUpFilesRecyclerView() {
        filesAdapter = AdminELearningFilesAdapter(parentFragmentManager, fileList, fileViewModel)

        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filesAdapter
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


    private fun parseJsonObject() {
        try {
            if (jsonData?.isNotBlank() == true) {
                jsonData?.let { json ->
                    JSONObject(json).let {
                        title = it.getString("title")
                        grade = it.getString("objective")
                        description = it.getString("description")
                        dueDate = formatDate2(it.getString("end_date"), "custom1")
                        parseFilesArray(JSONArray(it.getString("picref")))
                    }
                }

                setTextOnViews()

                setUpFilesRecyclerView()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTextOnViews() {
        if (dueDate?.isNotBlank() == true) {
            "Due $dueDate".let { dueDateTxt.text = it }
        }

        if (grade?.isNotBlank() == true) {
            "$grade points".let { gradeTxt.text = it }
        }

        titleTxt.text = title
        descriptionTxt.text = description
    }

    private fun parseFilesArray(files: JSONArray) {
        for (i in 0 until files.length()) {
            files.getJSONObject(i).let {
                val fileName = trimText(it.getString("file_name"))
                val oldFileName = trimText(it.getString("file_name"))
                val type = it.getString("type")
                val uri = it.getString("file_name")

                val attachmentModel = AttachmentModel(fileName, oldFileName, type, uri)

                fileList.add(attachmentModel)
            }
        }
    }

    private fun trimText(text: String): String {
        return text.replace("../assets/elearning/practice/", "").ifEmpty { "" }
    }

    private fun commentClick() {
        commentTxt.setOnClickListener {
            it.isVisible = false
            commentInput.isVisible = true

            commentInput.editText?.let { edit ->
                FunctionUtils.showSoftInput(
                    requireContext(),
                    edit
                )
            }
        }

    }

    private fun sendComment() {
        val message = commentInput.editText?.text.toString().trim()
        val date = formatDate2(FunctionUtils.getDate())

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
            } else if (actionId == EditorInfo.IME_ACTION_NONE) {
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

}