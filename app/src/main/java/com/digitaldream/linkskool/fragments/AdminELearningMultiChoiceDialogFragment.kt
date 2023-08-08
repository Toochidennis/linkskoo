package com.digitaldream.linkskool.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.AdminELearningMultiChoiceAdapter
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import java.io.File


class AdminELearningMultiChoiceDialogFragment(
    private val question: MultiChoiceQuestion,
    private val onQuestionSet: (question: MultiChoiceQuestion) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_multi_choice) {

    private lateinit var mDismissBtn: ImageView
    private lateinit var mAskBtn: Button
    private lateinit var mQuestionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mRemoveQuestionAttachmentBtn: ImageView
    private lateinit var mOptionsRecyclerView: RecyclerView
    private lateinit var mAddOptionBtn: TextView

    private lateinit var optionAdapter: AdminELearningMultiChoiceAdapter

    private val mOptionList = mutableListOf<MultipleChoiceOption>()
    private lateinit var questionModelCopy: MultiChoiceQuestion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        isCancelable = false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mDismissBtn = findViewById(R.id.close_btn)
            mAskBtn = findViewById(R.id.ask_btn)
            mQuestionEditText = findViewById(R.id.question_edit)
            mAttachmentTxt = findViewById(R.id.attachmentTxt)
            mAttachmentBtn = findViewById(R.id.attachment_btn)
            mRemoveQuestionAttachmentBtn = findViewById(R.id.removeQuestionAttachment)
            mOptionsRecyclerView = findViewById(R.id.options_recyclerview)
            mAddOptionBtn = findViewById(R.id.add_option_btn)
        }

        showSoftInput(requireContext(), mQuestionEditText)

        initializeQuestionModel()
        initializeOptions()
        setUpRecyclerView()

        mDismissBtn.setOnClickListener {
            onExit()
        }

        mAddOptionBtn.setOnClickListener {
            optionAdapter.addOption()
        }

        mAskBtn.setOnClickListener {
            askQuestion()
        }

        mRemoveQuestionAttachmentBtn.setOnClickListener {
            removeQuestionAttachment()
        }

        mAttachmentTxt.setOnClickListener {
            if (questionModelCopy.attachmentUri != null) {
                previewAttachment(questionModelCopy.attachmentUri!!)
            } else {
                showQuestionAttachment()
            }
        }
    }

    private fun initializeQuestionModel() {
        questionModelCopy = question.copy()
        mQuestionEditText.setText(questionModelCopy.questionText)

        if (questionModelCopy.attachmentName.isNotEmpty()) {
            setDrawableOnTextView(mAttachmentTxt)
            mAttachmentTxt.text = questionModelCopy.attachmentName
            mRemoveQuestionAttachmentBtn.isVisible = true
        }

    }

    private fun initializeOptions() {
        if (!questionModelCopy.options.isNullOrEmpty()) {
            mOptionList.addAll(questionModelCopy.options!!)
        } else {
            mOptionList.add(MultipleChoiceOption(""))
        }

    }

    private fun setUpRecyclerView() {
        optionAdapter = AdminELearningMultiChoiceAdapter(
            parentFragmentManager,
            mOptionList, questionModelCopy, mOptionsRecyclerView
        )

        mOptionsRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = optionAdapter
        }
    }

    private fun showQuestionAttachment() {
        AdminELearningAttachmentDialog("multiple choice")
        { _, name: String, uri: Any? ->
            try {
                questionModelCopy.attachmentName = name
                questionModelCopy.attachmentUri = uri

                setDrawableOnTextView(mAttachmentTxt)
                mAttachmentTxt.text = questionModelCopy.attachmentName
                mRemoveQuestionAttachmentBtn.isVisible = true
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.show(parentFragmentManager, "")
    }

    private fun removeQuestionAttachment() {
        questionModelCopy.attachmentUri = null
        questionModelCopy.attachmentName = ""
        mRemoveQuestionAttachmentBtn.isVisible = false
        "Add attachment".also { mAttachmentTxt.text = it }
        mAttachmentTxt.setCompoundDrawablesWithIntrinsicBounds(
            null, null, null, null
        )
    }

    private fun setDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_image24),
            null, null, null
        )
    }

    private fun previewAttachment(uri: Any) {
        try {
            val fileUri = when (uri) {
                is File -> {
                    val file = File(uri.absolutePath)
                    FileProvider.getUriForFile(
                        requireContext(),
                        "${requireActivity().packageName}.provider",
                        file
                    )
                }

                is String -> Uri.parse(uri)

                else -> uri
            }

            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri as Uri?, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.let {
                startActivity(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(), "Error occurred while viewing file", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onExit() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Are you sure to exit?")
            setMessage("Your unsaved changes will be lost")
            setPositiveButton("Yes") { _, _ ->
                dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }.create()
    }

    private fun askQuestion() {
        val questionText = mQuestionEditText.text.toString().trim()

        if (questionText.isEmpty()) {
            mQuestionEditText.error = "Please enter a question"
        } else {
            val isNotEmpty = optionAdapter.prepareOptions(requireContext())

            if (isNotEmpty) {
                questionModelCopy.questionText = questionText
                onQuestionSet(questionModelCopy)

                dismiss()
            }
        }
    }

}
