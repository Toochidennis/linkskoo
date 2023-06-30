package com.digitaldream.linkskool.fragments

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RadioButton
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
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.models.MultipleChoiceOption
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
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

    private lateinit var mOptionsAdapter: GenericAdapter<MultipleChoiceOption>

    private val mOptionList = mutableListOf<MultipleChoiceOption>()
    private lateinit var questionModelCopy: MultiChoiceQuestion
    private var selectedPosition = RecyclerView.NO_POSITION


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
        options()

        mDismissBtn.setOnClickListener {
            onDiscard()
        }

        mAddOptionBtn.setOnClickListener {
            addOption()
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

        if (questionModelCopy.checkedPosition != RecyclerView.NO_POSITION) {
            selectedPosition = questionModelCopy.checkedPosition
        }
    }


    private fun options() {
        mOptionsAdapter = GenericAdapter(
            mOptionList,
            R.layout.fragment_admin_e_learning_multi_choice_item,
            bindItem = { itemView, model, position ->
                val radioButton: RadioButton =
                    itemView.findViewById(R.id.radioButtonOption)
                val editText: EditText =
                    itemView.findViewById(R.id.editTextAnswer)
                val showAttachmentPopUpBtn: ImageView =
                    itemView.findViewById(R.id.removeOptionButton)
                val attachmentTxt: TextView =
                    itemView.findViewById(R.id.attachmentName)
                val removeAttachmentBtn: ImageView =
                    itemView.findViewById(R.id.removeAttachmentButton)

                smoothScrollEditText(editText)

                if (model.attachmentName.isEmpty()) {
                    editText.setText(model.optionText)
                    attachmentTxt.isVisible = false
                    editText.isVisible = true
                } else {
                    attachmentTxt.text = model.attachmentName
                    setDrawableOnTextView(attachmentTxt)
                    attachmentTxt.isVisible = true
                    removeAttachmentBtn.isVisible = true
                    showAttachmentPopUpBtn.isVisible = false
                    editText.isVisible = false
                }

                radioButton.isChecked = position == selectedPosition

                attachmentTxt.setOnClickListener {
                    previewAttachment(model.attachmentUri!!)
                }

                radioButton.setOnClickListener {
                    if (radioButton.isChecked) {
                        selectedPosition = position
                        questionModelCopy.checkedPosition = selectedPosition

                        questionModelCopy.correctAnswer =
                            model.optionText.ifEmpty { model.attachmentName }

                        updateRadioButtonState(position)
                    }
                }

                showAttachmentPopUpBtn.setOnClickListener { view ->
                    val popUpMenu = PopupMenu(view.context, view)
                    popUpMenu.inflate(R.menu.pop_menu)
                    popUpMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.attach -> {
                                AdminELearningAttachmentDialog("multiple choice")
                                { type: String, name: String, uri: Any? ->
                                    model.attachmentName = name
                                    model.attachmentType = type
                                    model.attachmentUri = uri
                                    model.optionText = ""
                                    model.optionOrder = "$position"

                                    if (selectedPosition == position)
                                        questionModelCopy.correctAnswer = model.attachmentName

                                    setDrawableOnTextView(attachmentTxt)

                                    attachmentTxt.isVisible = true
                                    removeAttachmentBtn.isVisible = true
                                    showAttachmentPopUpBtn.isVisible = false
                                    editText.isVisible = false

                                    mOptionsAdapter.notifyDataSetChanged()

                                }.show(parentFragmentManager, "")

                                true
                            }

                            R.id.delete -> {
                                removeOption(position)
                                true
                            }

                            else -> false
                        }
                    }

                    popUpMenu.show()
                }

                removeAttachmentBtn.setOnClickListener { view ->
                    val popUpMenu = PopupMenu(view.context, view)
                    popUpMenu.inflate(R.menu.detach_menu)
                    popUpMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.detach -> {
                                removeOption(position)
                                true
                            }

                            else -> false
                        }
                    }

                    popUpMenu.show()
                }

                editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        model.optionOrder = (position).toString()
                        model.optionText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })
            },

            onItemClick = {}
        )

        mOptionsRecyclerView.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mOptionsAdapter
        }
    }

    private fun removeOption(position: Int) {
        mOptionList.removeAt(position)
        mOptionsAdapter.notifyItemRemoved(position)
    }

    private fun addOption() {
        val optionText = ""
        val option = MultipleChoiceOption(optionText)
        mOptionList.add(option)
        mOptionsAdapter.notifyItemRangeInserted(mOptionList.size - 1, 1)
    }

    private fun showQuestionAttachment() {
        AdminELearningAttachmentDialog("multiple choice")
        { type: String, name: String, uri: Any? ->
            try {
                questionModelCopy.attachmentName = name
                questionModelCopy.attachmentType = type
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
        questionModelCopy.attachmentType = ""
        mRemoveQuestionAttachmentBtn.isVisible = false
        mAttachmentTxt.text = "Add attachment"
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

    private fun onDiscard() {
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
        } else if (selectedPosition == RecyclerView.NO_POSITION) {
            Toast.makeText(
                requireContext(), "Please select an option", Toast.LENGTH_SHORT
            ).show()
        } else if (mOptionList.isEmpty()) {
            Toast.makeText(
                requireContext(), "Please set at least one option", Toast.LENGTH_SHORT
            ).show()
        } else {
            questionModelCopy.questionText = questionText
            questionModelCopy.options = mOptionList

            onQuestionSet(questionModelCopy)
            println("question $questionModelCopy")

            dismiss()
        }

    }

    private fun updateRadioButtonState(position: Int) {
        val itemCount = mOptionsRecyclerView.childCount
        for (i in 0 until itemCount) {
            val itemView = mOptionsRecyclerView.getChildAt(i)
            val radioButton: RadioButton = itemView.findViewById(R.id.radioButtonOption)
            radioButton.isChecked = i == position
        }
    }

}
