package com.digitaldream.linkskool.dialog


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
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.models.ShortAnswerModel
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import java.io.File

class AdminELearningShortAnswerDialogFragment(
    private val shortAnswerModel: ShortAnswerModel,
    private val onAskQuestion: (question: ShortAnswerModel) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_short_answer) {

    private lateinit var dismissBtn: ImageView
    private lateinit var askBtn: Button
    private lateinit var questionEditText: EditText
    private lateinit var attachmentTxt: TextView
    private lateinit var attachmentBtn: RelativeLayout
    private lateinit var removeQuestionAttachmentBtn: ImageView
    private lateinit var answerEditText: EditText

    private lateinit var shortAnswerModelCopy: ShortAnswerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            dismissBtn = findViewById(R.id.dismissBtn)
            askBtn = findViewById(R.id.askBtn)
            questionEditText = findViewById(R.id.questionEditText)
            attachmentTxt = findViewById(R.id.attachmentTxt)
            attachmentBtn = findViewById(R.id.attachment_btn)
            removeQuestionAttachmentBtn = findViewById(R.id.removeQuestionAttachment)
            answerEditText = findViewById(R.id.answerEditText)
        }

        showSoftInput(requireContext(), questionEditText)

        initializeModel()
        showQuestionAttachment(attachmentBtn)

        removeQuestionAttachmentBtn.setOnClickListener {
            removeQuestionAttachment()
        }

        dismissBtn.setOnClickListener {
            onDiscard()
        }

        askBtn.setOnClickListener {
            askQuestion()
        }
    }

    private fun initializeModel() {
        shortAnswerModelCopy = shortAnswerModel.copy()
        questionEditText.setText(shortAnswerModelCopy.questionText)
        answerEditText.setText(shortAnswerModelCopy.answerText)

        if (shortAnswerModelCopy.attachmentName.isNotEmpty()) {
            setDrawableOnTextView(attachmentTxt)
            attachmentTxt.text = shortAnswerModelCopy.attachmentName
            removeQuestionAttachmentBtn.isVisible = true
            attachmentBtn.isClickable = false
        }
    }

    private fun showQuestionAttachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog("multiple choice") { type, name, uri ->
                try {
                    shortAnswerModelCopy.attachmentName = name
                    shortAnswerModelCopy.attachmentType = type
                    shortAnswerModelCopy.attachmentUri = uri

                    setDrawableOnTextView(attachmentTxt)
                    attachmentTxt.text = shortAnswerModelCopy.attachmentName
                    removeQuestionAttachmentBtn.isVisible = true
                    attachmentBtn.isClickable = false

                    attachmentTxt.setOnClickListener {
                        previewAttachment(shortAnswerModelCopy.attachmentUri!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.show(parentFragmentManager, "")
        }
    }

    private fun removeQuestionAttachment() {
        shortAnswerModelCopy.attachmentUri = null
        shortAnswerModelCopy.attachmentName = ""
        shortAnswerModelCopy.attachmentType = ""
        removeQuestionAttachmentBtn.isVisible = false
        attachmentTxt.text = "Add attachment"
        attachmentTxt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        attachmentTxt.isClickable = false
        attachmentBtn.isClickable = true
    }

    private fun setDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_image24),
            null,
            null,
            null
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
                requireContext(),
                "Error occurred while viewing the file",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onDiscard() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Are you sure you want to exit?")
            setMessage("Your unsaved changes will be lost.")
            setPositiveButton("Yes") { _, _ ->
                dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun askQuestion() {
        val questionText = questionEditText.text.toString().trim()
        val answerText = answerEditText.text.toString().trim()

        if (questionText.isEmpty()) {
            questionEditText.error = "Please enter a question"
        } else if (answerText.isEmpty()) {
            answerEditText.error = "Please enter an answer"
        } else {
            shortAnswerModelCopy.questionText = questionText
            shortAnswerModelCopy.answerText = answerText
            onAskQuestion(shortAnswerModelCopy)

            dismiss()
        }
    }
}
