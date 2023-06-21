package com.digitaldream.linkskool.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.digitaldream.linkskool.models.OptionsModel
import com.digitaldream.linkskool.utils.FunctionUtils.showSoftInput
import com.digitaldream.linkskool.utils.FunctionUtils.smoothScrollEditText
import java.io.File


class AdminELearningQuestionDialogFragment :
    DialogFragment(R.layout.fragment_admin_e_learning_multi_choice) {


    private lateinit var mDismissBtn: ImageView
    private lateinit var mAskBtn: Button
    private lateinit var mQuestionEditText: EditText
    private lateinit var mAttachmentTxt: TextView
    private lateinit var mAttachmentBtn: RelativeLayout
    private lateinit var mAttachmentRecyclerView: RecyclerView
    private lateinit var mAddAttachmentBtn: TextView
    private lateinit var mOptionsRecyclerview: RecyclerView
    private lateinit var mAddOptionBtn: TextView

    private lateinit var mOptionsAdapter: GenericAdapter<OptionsModel>
    private lateinit var mAttachmentAdapter: GenericAdapter<AttachmentModel>

    private val mOptionList = mutableListOf<OptionsModel>()
    private val mFileList = mutableListOf<AttachmentModel>()
    private var mAttachmentModel: AttachmentModel? = null

    private val optionItems = HashMap<String, Any>()


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
            mAttachmentRecyclerView = findViewById(R.id.attachment_recyclerview)
            mAddAttachmentBtn = findViewById(R.id.addAttachmentButton)
            mOptionsRecyclerview = findViewById(R.id.options_recyclerview)
            mAddOptionBtn = findViewById(R.id.add_option_btn)

        }

        options()

        attachment(mAttachmentBtn)
        attachment(mAddAttachmentBtn)

        showSoftInput(requireContext(), mQuestionEditText)

        mDismissBtn.setOnClickListener {
            dismiss()
        }

    }


    private fun options() {
        mOptionList.add(OptionsModel().apply {
            optionText = "Option 1"
        })

        var selectedPosition = RecyclerView.NO_POSITION

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
                radioButton.isChecked = position == selectedPosition

                radioButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedPosition = position
                        if (!mOptionsRecyclerview.isComputingLayout && mOptionsRecyclerview
                                .scrollState == RecyclerView
                                .SCROLL_STATE_IDLE
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
                                    mAttachmentModel = AttachmentModel(name, type, uri)
                                    attachmentTxt.text = mAttachmentModel?.name
                                    setDrawableOnTextView(attachmentTxt, mAttachmentModel!!.type)

                                    attachmentTxt.isVisible = true
                                    editText.isVisible = false
                                    removeOptionButton.isVisible = false
                                    removeAttachmentBtn.isVisible = true

                                    attachmentTxt.setOnClickListener {
                                        previewAttachment(mAttachmentModel!!)
                                    }

                                }.show(parentFragmentManager, "")

                                true
                            }

                            R.id.delete -> {
                                mOptionList.removeAt(position)
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
                                removeAttachmentBtn.isVisible = false
                                removeOptionButton.isVisible = true
                                editText.isVisible = true
                                mAttachmentModel = null

                                true
                            }

                            else -> false
                        }
                    }

                    popUpMenu.show()
                }
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
            mOptionList.add(OptionsModel().apply {
                optionText = "Option ${mOptionList.size + 1}"
                mOptionsAdapter.notifyItemInserted(mOptionList.size - 1)
                mOptionsAdapter.notifyDataSetChanged()
            })
        }
    }


    private fun attachment(button: View) {
        button.setOnClickListener {
            AdminELearningAttachmentDialog { type: String, name: String, uri: Any? ->
                try {
                    mFileList.add(AttachmentModel(name, type, uri))

                    if (mFileList.isNotEmpty()) {
                        mAttachmentAdapter = GenericAdapter(
                            mFileList,
                            R.layout.fragment_admin_e_learning_assigment_attachment_item,
                            bindItem = { itemView, model, position ->
                                val itemTxt: TextView = itemView.findViewById(R.id.itemTxt)
                                val deleteButton: ImageView =
                                    itemView.findViewById(R.id.deleteButton)

                                itemTxt.text = model.name
                                setDrawableOnTextView(itemTxt, model.type)

                                deleteButton.setOnClickListener {
                                    mFileList.removeAt(position)
                                    if (mFileList.isEmpty()) {
                                        mAttachmentTxt.isVisible = true
                                        mAddAttachmentBtn.isVisible = false
                                        mAttachmentBtn.isClickable = true
                                    }
                                    mAttachmentAdapter.notifyDataSetChanged()
                                }

                            }, onItemClick = { position: Int ->
                                val itemPosition = mFileList[position]

                                previewAttachment(itemPosition)
                            }
                        )

                        mAttachmentRecyclerView.apply {
                            hasFixedSize()
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = mAttachmentAdapter
                            scrollToPosition(mFileList.size - 1)

                            mAttachmentTxt.isVisible = false
                            mAddAttachmentBtn.isVisible = true
                            mAttachmentBtn.isClickable = false
                        }
                    } else {
                        mAttachmentTxt.isVisible = true
                        mAddAttachmentBtn.isVisible = false
                        mAttachmentBtn.isClickable = true
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


}
