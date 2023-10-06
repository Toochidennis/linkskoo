package com.digitaldream.linkskool.dialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
import com.digitaldream.linkskool.utils.FunctionUtils.isBased64
import com.digitaldream.linkskool.utils.StudentFileViewModel
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

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
    private lateinit var fileAdapter: StudentELearningAssignmentSubmissionAdapter
    private lateinit var studentFileViewModel: StudentFileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private var savedJson: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

        studentFileViewModel =
            ViewModelProvider(requireActivity())[StudentFileViewModel::class.java]
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

        sharedPreferences = requireActivity().getSharedPreferences("loginDetail", MODE_PRIVATE)
        savedJson = sharedPreferences.getString("attachment", "")
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

                when (type) {
                    "image", "pdf", "excel", "word" -> {
                        val byteArray = convertUriToByteArray(uri)
                        Timber.tag("response").d("$byteArray")
                        fileList.add(AttachmentModel(name, "", type, byteArray, true))

                        saveFile()

                        fileAdapter.notifyDataSetChanged()
                    }

                    else -> Toast.makeText(
                        requireContext(),
                        "File type not supported",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }.show(parentFragmentManager, "")
        }
    }

    private fun setUpFileRecyclerView() {
        readSavedFile()

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

    private fun convertUriToByteArray(uri: Any?): Any? {
        try {
            val inputStream = when (uri) {
                is File -> FileInputStream(uri)
                is Uri -> requireActivity().contentResolver.openInputStream(uri)
                else -> null
            }

            inputStream.use { input ->
                val outputStream = ByteArrayOutputStream()
                val bufferedInput = BufferedInputStream(input)
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (bufferedInput.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                return outputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun saveFile() {
        val fileArray = JSONArray()

        fileList.forEach { file ->
            JSONObject().apply {
                put("name", file.name)
                put("type", file.type)

                val encodedFile = Base64.encodeToString(file.uri as ByteArray, Base64.DEFAULT)
                put("uri", encodedFile)
            }.let {
                fileArray.put(it)
            }
        }

        sharedPreferences.edit().putString("attachment", fileArray.toString()).apply()
    }

    private fun readSavedFile() {
        if (savedJson?.isNotBlank() == true)
            with(JSONArray(savedJson)) {
                for (i in 0 until length()) {
                    getJSONObject(i).let {
                        val name = it.getString("name")
                        val type = it.getString("type")
                        val uri = it.getString("uri")

                        val isBase64 = isBased64(uri)

                        if (isBase64) {
                            val decodedByte = Base64.decode(uri, Base64.DEFAULT)

                            fileList.add(AttachmentModel(name, "", type, decodedByte))
                        }
                    }
                }
            }
    }

}