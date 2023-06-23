package com.digitaldream.linkskool.fragments

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaldream.linkskool.R
import com.digitaldream.linkskool.adapters.GenericAdapter
import com.digitaldream.linkskool.dialog.AdminELearningAttachmentDialog
import com.digitaldream.linkskool.models.AttachmentModel
import com.digitaldream.linkskool.models.MultiChoiceOption
import com.digitaldream.linkskool.models.MultiChoiceQuestion
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
import java.io.File


class AdminELearningMultiChoiceDialogFragment(
    /*  private val askQuestion: (question: MultiChoiceQuestion) -> Unit*/
) : DialogFragment(R.layout.fragment_admin_e_learning_multi_choice) {

    private lateinit var mDismissBtn: ImageView
    private lateinit var mAskBtn: Button
    private lateinit var mQuestionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mRemoveQuestionAttachmentBtn: ImageView
    private lateinit var mOptionsRecyclerview: RecyclerView
    private lateinit var mAddOptionBtn: TextView

    private lateinit var mOptionsAdapter: GenericAdapter<MultiChoiceOption>

    private val mOptionList = mutableListOf<MultiChoiceOption>()
    private val mFileList = mutableListOf<AttachmentModel>()
    private val optionList = HashMap<String, Any>()
    private val correctList = HashMap<String, Any>()

    private var mOptionAttachmentModel: AttachmentModel? = null
    private var mQuestionAttachmentModel: AttachmentModel? = null
    private val mQuestionModel: MultiChoiceQuestion? = null

    private var isTextChanged = false
    private var selectedPosition = RecyclerView.NO_POSITION


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            mDismissBtn = findViewById(R.id.close_btn)
            mAskBtn = findViewById(R.id.ask_btn)
            mQuestionEditText = findViewById(R.id.question_edit)
            mAttachmentTxt = findViewById(R.id.attachmentTxt)
            mAttachmentBtn = findViewById(R.id.attachment_btn)
            mRemoveQuestionAttachmentBtn = findViewById(R.id.removeQuestionAttachment)
            mOptionsRecyclerview = findViewById(R.id.options_recyclerview)
            mAddOptionBtn = findViewById(R.id.add_option_btn)
        }

        options()

        attachment(mAttachmentBtn)

        showSoftInput(requireContext(), mQuestionEditText)

        mDismissBtn.setOnClickListener {
            dismiss()
        }

        mAskBtn.setOnClickListener {
            askQuestion()
        }

    }


    private fun options() {
        mOptionList.add(MultiChoiceOption().apply {
            optionText = "Option 1"
        })

        mOptionsAdapter = GenericAdapter(
            mOptionList,
            R.layout.fragment_admin_e_learning_multi_choice_item,
            bindItem = { itemView, model, position ->
                val radioButton: RadioButton =
                    itemView.findViewById(R.id.radioButtonOption)
                val editText: EditText =
                    itemView.findViewById(R.id.editTextAnswer)
                val removeOptionButton: ImageView =
                    itemView.findViewById(R.id.removeOptionButton)
                val attachmentTxt: TextView =
                    itemView.findViewById(R.id.attachmentName)
                val removeAttachmentBtn: ImageView =
                    itemView.findViewById(R.id.removeAttachmentButton)

                editText.setText(model.optionText)
                smoothScrollEditText(editText)

                if (selectedPosition != RecyclerView.NO_POSITION) {
                    optionList[model.optionOrder ?: ""] = model.optionText ?: ""
                }


                if (!model.optionImageName.isNullOrEmpty()) {
                    setDrawableOnTextView(attachmentTxt, model.optionImageType!!)
                    mOptionAttachmentModel = AttachmentModel(
                        model.optionImageName!!,
                        model.optionImageType!!,
                        model.optionImageUri
                    )

                    // correctList[mQuestionModel!!.correctAnswerOrder] = model.optionImageName!!

                    attachmentTxt.isVisible = true
                    editText.isVisible = false
                    removeOptionButton.isVisible = false
                    removeAttachmentBtn.isVisible = true
                }

                radioButton.isChecked = position == selectedPosition


                radioButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        correctList.clear()
                        selectedPosition = position

                        if (attachmentTxt.text != "TODO") {
                            correctList["$selectedPosition"] = " model.optionImageName!!"
                        } else {
                            correctList["$selectedPosition"] = editText.text.toString().trim()
                        }


                        if (!mOptionsRecyclerview.isComputingLayout && mOptionsRecyclerview
                                .scrollState == RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            mOptionsAdapter.notifyDataSetChanged()
                        }
                    }
                }

                removeOptionButton.setOnClickListener { view ->
                    val popUpMenu = PopupMenu(view.context, view)
                    popUpMenu.inflate(R.menu.pop_menu)
                    popUpMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.attach -> {
                                AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                                    mOptionAttachmentModel = AttachmentModel(name, type, uri)
                                    attachmentTxt.text = mOptionAttachmentModel?.name
                                    setDrawableOnTextView(
                                        attachmentTxt,
                                        mOptionAttachmentModel!!.type
                                    )

                                    if (selectedPosition == position)
                                        correctList["$selectedPosition"] =
                                            mOptionAttachmentModel?.name!!

                                    attachmentTxt.isVisible = true
                                    editText.isVisible = false
                                    removeOptionButton.isVisible = false
                                    removeAttachmentBtn.isVisible = true


                                    attachmentTxt.setOnClickListener {
                                        previewAttachment(mOptionAttachmentModel!!)
                                    }

                                }.show(parentFragmentManager, "")

                                true
                            }

                            R.id.delete -> {
                                mOptionList.removeAt(position)
                                optionList.remove("$position")
                                mOptionsAdapter.notifyDataSetChanged()

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
                                attachmentTxt.isVisible = false
                                attachmentTxt.text = ""
                                removeAttachmentBtn.isVisible = false
                                removeOptionButton.isVisible = true
                                editText.isVisible = true
                                mOptionAttachmentModel = null

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
                        isTextChanged = true

                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (isTextChanged) {
                            isTextChanged = false

                            try {
                                optionList["$position"] = s.toString().trim()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                optionList["$position"] = ""
                            }
                        }

                    }
                })
            },

            onItemClick = {}
        )

        val temAnimator = DefaultItemAnimator()
        temAnimator.supportsChangeAnimations = true

        mOptionsRecyclerview.apply {
            itemAnimator = temAnimator
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mOptionsAdapter
            scrollToPosition(mOptionList.size - 1)
            hasFixedSize()
            isAnimating
        }


        mAddOptionBtn.setOnClickListener {
            mOptionList.add(
                MultiChoiceOption().apply {
                    optionText = "Option ${mOptionList.size + 1}"
                }
            )

            mOptionsAdapter.notifyItemInserted(mOptionList.size + 1)
        }
    }


    private fun attachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                try {
                    mQuestionAttachmentModel = AttachmentModel(name, type, uri)
                    setDrawableOnTextView(mAttachmentTxt, mQuestionAttachmentModel!!.type)
                    mAttachmentTxt.text = mQuestionAttachmentModel!!.name
                    mRemoveQuestionAttachmentBtn.isVisible = true
                    mAttachmentBtn.isClickable = false

                    mAttachmentTxt.setOnClickListener {
                        previewAttachment(mQuestionAttachmentModel!!)
                    }

                    mRemoveQuestionAttachmentBtn.setOnClickListener {
                        mRemoveQuestionAttachmentBtn.isVisible = false
                        mAttachmentTxt.text = "Add attachment"
                        mAttachmentTxt.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null, null, null
                        )
                        mAttachmentTxt.isClickable = false
                        mAttachmentBtn.isClickable = true

                        mQuestionAttachmentModel = null
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }.show(parentFragmentManager, "")
        }

    }

    private fun setDrawableOnTextView(textView: TextView, type: String) {
        textView.setCompoundDrawablesWithIntrinsicBounds(
            when (type) {
                "image" -> R.drawable.ic_image24
                "video" -> R.drawable.ic_video24
                "pdf" -> R.drawable.ic_pdf24
                "unknown" -> R.drawable.ic_unknown_document24
                "url" -> R.drawable.ic_link
                else -> R.drawable.ic_document24
            }.let {
                ContextCompat.getDrawable(requireContext(), it)
            },
            null, null, null
        )
    }

    private fun previewAttachment(item: AttachmentModel) {
        val fileUri = if (item.uri is File) {
            val file = File(item.uri.absolutePath)
            FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.provider",
                file
            )
        } else {
            item.uri
        }

        when (item.type) {
            "image" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "video" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            "url" -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(fileUri.toString())
                )
                startActivity(intent)
            }

            "pdf", "excel", "word" -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri as Uri?, "application/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }

            else -> {
                Toast.makeText(
                    requireContext(), "Can't open file", Toast
                        .LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun askQuestion() {
        val optionsModel = MultiChoiceOption()
        val questionText = mQuestionEditText.text.toString().trim().ifEmpty {
            mQuestionEditText.error = "Question is required!"
        }


        optionList.forEach { (key, value) ->
            optionsModel.apply {
                optionOrder = key
                if (value is String)
                    optionText = value
                optionImageName = mOptionAttachmentModel?.name ?: ""
                optionImageType = mOptionAttachmentModel?.type ?: ""
                optionImageUri = mOptionAttachmentModel?.uri ?: ""
            }
        }


        MultiChoiceQuestion(
            questionText.toString(),
            optionsModel,
            correctList.keys.toString(),
            correctList.values.toString()
        )

        println("option: $optionList  correct: $correctList")
    }


}
