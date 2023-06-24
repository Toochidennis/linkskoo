package com.digitaldream.linkskool.fragments

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
import com.digitaldream.linkskool.adapters.GenericAdapter2
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.models.MultiChoiceOption
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
import java.io.File


class AdminELearningMultiChoiceDialogFragment(
    private val question: MultiChoiceQuestion,
    private val askQuestion: (question: MultiChoiceQuestion) -> Unit
) : DialogFragment(R.layout.fragment_admin_e_learning_multi_choice) {

    private lateinit var mDismissBtn: ImageView
    private lateinit var mAskBtn: Button
    private lateinit var mQuestionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mRemoveQuestionAttachmentBtn: ImageView
    private lateinit var mOptionsRecyclerView: RecyclerView
    private lateinit var mAddOptionBtn: TextView

    private lateinit var mOptionsAdapter: GenericAdapter2<MultiChoiceOption>

    private val mOptionList = mutableListOf<MultiChoiceOption>()
    private lateinit var mQuestionModel: MultiChoiceQuestion
    private var selectedPosition = RecyclerView.NO_POSITION


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

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
        showQuestionAttachment(mAttachmentBtn)

        mDismissBtn.setOnClickListener {
            dismiss()
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


    }

    private fun initializeQuestionModel() {
        mQuestionModel = question.copy()
        mQuestionEditText.setText(mQuestionModel.questionText)

        if (mQuestionModel.attachmentName.isNotEmpty()) {
            setDrawableOnTextView(mAttachmentTxt)
            mAttachmentTxt.text = mQuestionModel.attachmentName
            mRemoveQuestionAttachmentBtn.isVisible = true
            mAttachmentBtn.isClickable = false
        }

    }

    private fun initializeOptions() {
        if (!mQuestionModel.options.isNullOrEmpty()) {
            mOptionList.addAll(mQuestionModel.options!!)
        } else {
            mOptionList.add(MultiChoiceOption("Option 1"))
        }

        if (mQuestionModel.checkedPosition != RecyclerView.NO_POSITION) {
            selectedPosition = mQuestionModel.checkedPosition
        }
    }


    private fun options() {
        mOptionsAdapter = GenericAdapter2(
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

                if (model.optionText.isEmpty()) {
                    attachmentTxt.text = model.attachmentName
                    attachmentTxt.visibility = View.VISIBLE
                    editText.visibility = View.GONE
                } else {
                    editText.setText(model.optionText)
                    editText.visibility = View.VISIBLE
                    attachmentTxt.visibility = View.GONE
                }

                radioButton.isChecked = position == selectedPosition

                radioButton.setOnClickListener {
                    if (radioButton.isChecked) {
                        selectedPosition = position
                        mQuestionModel.checkedPosition = selectedPosition

                        mQuestionModel.correctAnswer =
                            model.optionText.ifEmpty { model.attachmentName }

                        if (!mOptionsRecyclerView.isComputingLayout &&
                            mOptionsRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            mOptionsAdapter.notifyDataSetChanged()
                        }
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
                                    attachmentTxt.text = name
                                    model.attachmentName = name
                                    model.attachmentType = type
                                    model.attachmentUri = uri
                                    model.optionText = ""
                                    model.optionOrder = "$position"

                                    if (selectedPosition == position)
                                        mQuestionModel.correctAnswer = model.attachmentName

                                    setDrawableOnTextView(attachmentTxt)

                                    attachmentTxt.isVisible = true
                                    removeAttachmentBtn.isVisible = true
                                    showAttachmentPopUpBtn.isVisible = false
                                    editText.isVisible = false


                                    attachmentTxt.setOnClickListener {
                                        previewAttachment(model.attachmentUri!!)
                                    }

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
                                attachmentTxt.visibility = View.GONE
                                removeAttachmentBtn.visibility = View.GONE
                                showAttachmentPopUpBtn.visibility = View.VISIBLE
                                editText.visibility = View.VISIBLE

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
                        model.optionOrder = "$position"
                        model.optionText = s.toString()
                    }

                    override fun afterTextChanged(s: Editable?) {

                    }
                })
            }
        )

        mOptionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mOptionsAdapter
            hasFixedSize()
        }
    }

    private fun removeOption(position: Int) {
        mOptionList.removeAt(position)
        mOptionsAdapter.notifyDataSetChanged()
    }

    private fun addOption() {
        mOptionList.add(MultiChoiceOption("Option ${mOptionList.size + 1}"))
        mOptionsAdapter.notifyItemInserted(mOptionList.size + 1)
        mOptionsRecyclerView.smoothScrollToPosition(mOptionList.size - 1)
    }


    private fun showQuestionAttachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog("multiple choice")
            { type: String, name: String, uri: Any? ->
                try {
                    mQuestionModel.attachmentName = name
                    mQuestionModel.attachmentType = type
                    mQuestionModel.attachmentUri = uri

                    setDrawableOnTextView(mAttachmentTxt)
                    mAttachmentTxt.text = mQuestionModel.attachmentName
                    mRemoveQuestionAttachmentBtn.isVisible = true
                    mAttachmentBtn.isClickable = false

                    mAttachmentTxt.setOnClickListener {
                        previewAttachment(mQuestionModel.attachmentUri!!)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }.show(parentFragmentManager, "")
        }

    }

    private fun removeQuestionAttachment() {
        mQuestionModel.attachmentUri = null
        mQuestionModel.attachmentName = ""
        mQuestionModel.attachmentType = ""
        mRemoveQuestionAttachmentBtn.isVisible = false
        mAttachmentTxt.text = "Add attachment"
        mAttachmentTxt.setCompoundDrawablesWithIntrinsicBounds(
            null, null, null, null
        )
        mAttachmentTxt.isClickable = false
        mAttachmentBtn.isClickable = true
    }

    private fun setDrawableOnTextView(textView: TextView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_image24),
            null, null, null
        )
    }

    private fun previewAttachment(uri: Any) {
        val fileUri = if (uri is File) {
            val file = File(uri.absolutePath)
            FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.provider",
                file
            )
        } else {
            uri
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri as Uri?, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)

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
            mQuestionModel.questionText = questionText
            mQuestionModel.options = mOptionList

            println("question $mQuestionModel")
        }

    }

}
