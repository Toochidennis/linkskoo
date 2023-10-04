package com.digitaldream.linkskool.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningCommentAdapter
import com.digitaldream.linkskool.adapters.StudentELearningAssignmentSubmissionAdapter
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.CommentDataModel
import com.digitaldream.linkskool.utils.FunctionUtils
import com.digitaldream.linkskool.utils.StudentFileViewModel
import com.google.android.material.textfield.TextInputLayout

class StudentELearningAssignmentSubmissionDialogFragment :
    DialogFragment(R.layout.fragment_student_e_learning_assignment_submission) {

    private lateinit var backBtn: ImageButton
    private lateinit var dateTxt: TextView
    private lateinit var attachmentRecyclerView: RecyclerView
    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var addCommentTxt: TextView
    private lateinit var commentInput: TextInputLayout
    private lateinit var addWorkBtn: Button
    private lateinit var handInBtn: Button

    private lateinit var commentAdapter: AdminELearningCommentAdapter
    private val commentList = mutableListOf<CommentDataModel>()

    private val fileList = mutableListOf<AttachmentModel>()
    private val deletedFileList = mutableListOf<AttachmentModel>()
    private lateinit var fileAdapter: StudentELearningAssignmentSubmissionAdapter
    private lateinit var studentFileViewModel: StudentFileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        studentFileViewModel = ViewModelProvider(requireActivity())[StudentFileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews(view)

        commentAction()

        attachmentAction()
    }

    private fun setUpViews(view: View) {
        view.apply {
            backBtn = findViewById(R.id.backBtn)
            dateTxt = findViewById(R.id.dateTxt)
            attachmentRecyclerView = findViewById(R.id.attachmentRecyclerView)
            commentRecyclerView = findViewById(R.id.commentRecyclerView)
            addCommentTxt = findViewById(R.id.addCommentTxt)
            commentInput = findViewById(R.id.commentInputText)
            addWorkBtn = findViewById(R.id.addWorkBtn)
            handInBtn = findViewById(R.id.handInBtn)
        }
    }

    private fun commentAction() {
        setUpCommentRecyclerView()

        commentClick()

        addComment()
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
        addCommentTxt.setOnClickListener {
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
        val date = FunctionUtils.formatDate2(FunctionUtils.getDate())

        if (message.isNotBlank()) {
            val commentDataModel = CommentDataModel("id", "id", "Toochi Dennis", message, date)
            commentList.add(commentDataModel)

            commentInput.isVisible = false
            addCommentTxt.isVisible = true

            commentInput.editText?.let { hideKeyboard(it) }

            commentAdapter.notifyDataSetChanged()
        } else {
            commentInput.error = "Please provide a comment"
        }
    }

    private fun addComment() {
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

    private fun attachmentAction() {
        setUpFileRecyclerView()

        fileAttachment()
    }

    private fun fileAttachment() {
        addWorkBtn.setOnClickListener {
            AdminELearningAttachmentDialog("student") { type: String, name: String, uri: Any? ->
                fileList.add(AttachmentModel(name, "", type, uri, true))

                fileAdapter.notifyDataSetChanged()

            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpFileRecyclerView() {
        fileAdapter = StudentELearningAssignmentSubmissionAdapter(
            fileList,
            studentFileViewModel,
            parentFragmentManager,
            viewLifecycleOwner
        )

        attachmentRecyclerView.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = fileAdapter
        }
    }

}